#include <windows.h>
#include <jni.h>
#include <jvmti.h>
#include <cstdint>
#include <stdexcept>
#include <string>
#include <unordered_map>
#include <cstdio>
#include <vector>
#include <map>
#include <unordered_set>
#include <fstream>

static unsigned char* Module = nullptr;
static jobject Loader = nullptr;
static const JNINativeInterface_* TraceOriginal=nullptr;
static jmethodID JNICALL TraceMethod(JNIEnv* e,jclass c,const char* n,const char* s){auto r=TraceOriginal->GetMethodID(e,c,n,s);std::printf("[jni-ui] Method %s %s = %p\n",n,s,r);return r;}
static jfieldID JNICALL TraceField(JNIEnv* e,jclass c,const char* n,const char* s){auto r=TraceOriginal->GetStaticFieldID(e,c,n,s);std::printf("[jni-ui] Static field %s %s = %p\n",n,s,r);return r;}
static jobject JNICALL TraceStatic(JNIEnv* e,jclass c,jfieldID f){auto r=TraceOriginal->GetStaticObjectField(e,c,f);std::printf("[jni-ui] Static value = %p\n",r);return r;}
static jobject JNICALL TraceObject(JNIEnv* e,jobject o,jmethodID m,va_list a){auto r=TraceOriginal->CallObjectMethodV(e,o,m,a);std::printf("[jni-ui] Object method %p = %p\n",m,r);return r;}
static jint JNICALL TraceInt(JNIEnv* e,jobject o,jmethodID m,va_list a){auto r=TraceOriginal->CallIntMethodV(e,o,m,a);std::printf("[jni-ui] Int method %p = %d\n",m,r);return r;}
static jclass LocalClass(JNIEnv* env,jobject,jstring input) {
    if(!input)return nullptr;
    const char* chars=env->GetStringUTFChars(input,nullptr);if(!chars)return nullptr;
    std::string name(chars);env->ReleaseStringUTFChars(input,chars);
    for(char& c:name)if(c=='/')c='.';
    jclass api=env->FindClass("java/lang/Class");if(!api)return nullptr;
    jmethodID method=env->GetStaticMethodID(api,"forName","(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;");
    if(!method){env->DeleteLocalRef(api);return nullptr;}
    jstring value=env->NewStringUTF(name.c_str());
    jclass result=static_cast<jclass>(env->CallStaticObjectMethod(api,method,value,JNI_FALSE,Loader));
    if(env->ExceptionCheck()){std::printf("[portable] Class lookup failed: %s\n",name.c_str());std::fflush(stdout);env->ExceptionDescribe();}
    env->DeleteLocalRef(value);env->DeleteLocalRef(api);return result;
}
struct ModuleRecord { int id; std::string klass, name, description; int category; };
struct SettingClass { int id; std::string klass; };
struct TypeInfo { void* type; std::string descriptor; };
struct FieldInfo { std::string klass; jobject owner; std::string key; };
static_assert(sizeof(ModuleRecord)==112 && sizeof(SettingClass)==40 && sizeof(FieldInfo)==72);
static std::vector<ModuleRecord> Modules;
static std::vector<SettingClass> SettingTypes;
static std::unordered_set<std::string> SettingKeys;
static std::unordered_map<std::string,std::string> FieldKeys;
static bool LocalModules(std::vector<ModuleRecord>& output) { output=Modules;std::printf("[portable] Local modules=%zu\n",output.size());std::fflush(stdout);return !output.empty(); }
static bool LocalTypes(std::vector<SettingClass>& output) { output=SettingTypes;std::printf("[portable] Local setting types=%zu\n",output.size());std::fflush(stdout);return !output.empty(); }
static bool LocalFields(std::map<int,FieldInfo> input,std::map<int,FieldInfo>& fields,void* settingsCopy) {
    struct Cleanup { void* data; ~Cleanup(){reinterpret_cast<void(*)(void*)>(Module+0x160f70)(data);} } cleanup{settingsCopy};
    fields.clear();
    for(const auto& item:input){auto found=FieldKeys.find(item.second.key);if(found==FieldKeys.end()||!SettingKeys.count(found->second)){std::printf("[portable] Missing field binding %s\n",item.second.key.c_str());std::fflush(stdout);return false;}auto value=item.second;value.key=found->second;fields.emplace(item.first,std::move(value));}
    std::printf("[portable] Local field bindings=%zu/%zu\n",fields.size(),input.size());std::fflush(stdout);
    return fields.size()==686;
}
static void Redirect(unsigned char* source,void* target) {
    unsigned char code[12]={0x48,0xb8};std::memcpy(code+2,&target,8);code[10]=0xff;code[11]=0xe0;
    DWORD old=0;if(!VirtualProtect(source,sizeof(code),PAGE_EXECUTE_READWRITE,&old))throw std::runtime_error("Cannot install local delivery adapter");
    std::memcpy(source,code,sizeof(code));FlushInstructionCache(GetCurrentProcess(),source,sizeof(code));
    DWORD ignored=0;if(!VirtualProtect(source,sizeof(code),old,&ignored))throw std::runtime_error("Cannot restore code protection");
}
static std::uint32_t ReadNumber(std::istream& in) { std::uint32_t value=0;in.read(reinterpret_cast<char*>(&value),4);if(!in)throw std::runtime_error("Truncated registry");return value; }
static std::string ReadText(std::istream& in) { auto n=ReadNumber(in);if(n>65535)throw std::runtime_error("Registry text quota");std::string s(n,'\0');in.read(s.data(),n);if(!in)throw std::runtime_error("Truncated registry text");return s; }
static std::wstring Wide(JNIEnv* env,jstring text) { const jchar* c=env->GetStringChars(text,nullptr);if(!c)throw std::runtime_error("Cannot read path");std::wstring s(reinterpret_cast<const wchar_t*>(c),env->GetStringLength(text));env->ReleaseStringChars(text,c);return s; }
static_assert(sizeof(std::string) == 32, "MSVC string ABI mismatch");
static_assert(sizeof(std::unordered_map<std::string, std::string>) == 64, "MSVC map ABI mismatch");

static void Fail(JNIEnv* env, const std::string& message) {
    jclass type = env->FindClass("java/lang/IllegalStateException");
    if (type) env->ThrowNew(type, message.c_str());
}
extern "C" JNIEXPORT void JNICALL Java_bench_prestige_portable_Bridge_monitorEnter(JNIEnv* env,jclass,jobject value) {
    if(!value){env->ThrowNew(env->FindClass("java/lang/NullPointerException"),"monitor");return;}
    if(env->MonitorEnter(value)!=JNI_OK&&!env->ExceptionCheck())Fail(env,"Cannot enter JVM monitor");
}
extern "C" JNIEXPORT void JNICALL Java_bench_prestige_portable_Bridge_monitorExit(JNIEnv* env,jclass,jobject value) {
    if(!value){env->ThrowNew(env->FindClass("java/lang/NullPointerException"),"monitor");return;}
    if(env->MonitorExit(value)!=JNI_OK&&!env->ExceptionCheck())Fail(env,"Cannot exit JVM monitor");
}
static std::string Utf8(JNIEnv* env, jstring text) {
    if (!text) throw std::runtime_error("Missing string");
    const char* chars = env->GetStringUTFChars(text, nullptr);
    if (!chars) throw std::runtime_error("Cannot read Java string");
    std::string result(chars); env->ReleaseStringUTFChars(text, chars); return result;
}
static void StoreJavaField(JNIEnv* env,jclass owner,jobject reflected,jobject receiver,jobject value,bool isStatic) {
    if(!owner||!reflected||(!isStatic&&!receiver)){env->ThrowNew(env->FindClass("java/lang/NullPointerException"),"field store");return;}
    jclass fieldClass=env->GetObjectClass(reflected);
    jint modifiers=env->CallIntMethod(reflected,env->GetMethodID(fieldClass,"getModifiers","()I"));
    if(env->ExceptionCheck())return;
    if(((modifiers&8)!=0)!=isStatic){env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"),"Field storage kind mismatch");return;}
    auto holder=static_cast<jclass>(env->CallObjectMethod(reflected,env->GetMethodID(fieldClass,"getDeclaringClass","()Ljava/lang/Class;")));
    if(env->ExceptionCheck())return;
    if(!env->IsSameObject(owner,holder)||(!isStatic&&!env->IsInstanceOf(receiver,holder))){env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"),"Field owner mismatch");return;}
    jfieldID id=env->FromReflectedField(reflected);if(!id||env->ExceptionCheck())return;
    auto type=static_cast<jclass>(env->CallObjectMethod(reflected,env->GetMethodID(fieldClass,"getType","()Ljava/lang/Class;")));
    if(env->ExceptionCheck())return;
    jclass classClass=env->FindClass("java/lang/Class");
    jboolean primitive=env->CallBooleanMethod(type,env->GetMethodID(classClass,"isPrimitive","()Z"));
    if(env->ExceptionCheck())return;
    if(!primitive){
        if(value&&!env->IsInstanceOf(value,type)){env->ThrowNew(env->FindClass("java/lang/ClassCastException"),"Field value type mismatch");return;}
        if(isStatic)env->SetStaticObjectField(holder,id,value);else env->SetObjectField(receiver,id,value);
        return;
    }
    if(!value){env->ThrowNew(env->FindClass("java/lang/NullPointerException"),"primitive field value");return;}
    auto name=static_cast<jstring>(env->CallObjectMethod(type,env->GetMethodID(classClass,"getName","()Ljava/lang/String;")));
    if(env->ExceptionCheck())return;
    std::string key=Utf8(env,name);jclass boxed=env->GetObjectClass(value);
#define STORE_PRIMITIVE(Name, Key, UnboxName, Signature) \
    if(key==Key){ \
        jmethodID unbox=env->GetMethodID(boxed,UnboxName,Signature);if(!unbox)return; \
        auto number=env->Call##Name##Method(value,unbox);if(env->ExceptionCheck())return; \
        if(isStatic)env->SetStatic##Name##Field(holder,id,number);else env->Set##Name##Field(receiver,id,number); \
        return; \
    }
    STORE_PRIMITIVE(Boolean,"boolean","booleanValue","()Z")
    STORE_PRIMITIVE(Byte,"byte","byteValue","()B")
    STORE_PRIMITIVE(Char,"char","charValue","()C")
    STORE_PRIMITIVE(Short,"short","shortValue","()S")
    STORE_PRIMITIVE(Int,"int","intValue","()I")
    STORE_PRIMITIVE(Long,"long","longValue","()J")
    STORE_PRIMITIVE(Float,"float","floatValue","()F")
    STORE_PRIMITIVE(Double,"double","doubleValue","()D")
#undef STORE_PRIMITIVE
    Fail(env,"Unsupported primitive field type");
}
extern "C" JNIEXPORT void JNICALL Java_bench_prestige_portable_Bridge_setStaticField(JNIEnv* env,jclass,jclass owner,jobject field,jobject value) {
    StoreJavaField(env,owner,field,nullptr,value,true);
}
extern "C" JNIEXPORT void JNICALL Java_bench_prestige_portable_Bridge_setInstanceField(JNIEnv* env,jclass,jclass owner,jobject field,jobject receiver,jobject value) {
    StoreJavaField(env,owner,field,receiver,value,false);
}

extern "C" JNIEXPORT void JNICALL Java_bench_prestige_portable_Bridge_open(
    JNIEnv* env, jclass, jstring nativePath, jstring registryPath, jstring fieldsPath, jclass nativeClass, jobject loader, jobjectArray names, jobjectArray values) {
    try {
        if (Module) throw std::runtime_error("Native runtime already initialized");
        if (!nativeClass || !loader || !names || !values) throw std::runtime_error("Missing bootstrap input");
        jsize count = env->GetArrayLength(names);
        if (count != 55 || env->GetArrayLength(values) != count) throw std::runtime_error("Wrong mapping census");
        std::ifstream registry(Wide(env,registryPath),std::ios::binary);
        if(!registry||ReadNumber(registry)!=0x50524d31||ReadNumber(registry)!=121)throw std::runtime_error("Invalid module registry");
        for(int i=0;i<121;i++){ModuleRecord m;m.id=ReadNumber(registry);m.klass=ReadText(registry);m.name=ReadText(registry);m.description=ReadText(registry);m.category=ReadNumber(registry);Modules.push_back(std::move(m));}
        if(registry.peek()!=EOF)throw std::runtime_error("Trailing registry data");
        std::ifstream fieldRegistry(Wide(env,fieldsPath),std::ios::binary);
        if(!fieldRegistry||ReadNumber(fieldRegistry)!=0x50524631||ReadNumber(fieldRegistry)!=686)throw std::runtime_error("Invalid field registry");
        for(int i=0;i<686;i++){auto field=ReadText(fieldRegistry);auto key=ReadText(fieldRegistry);if(key.size()!=64||!FieldKeys.emplace(field,key).second)throw std::runtime_error("Invalid field binding");}
        if(fieldRegistry.peek()!=EOF)throw std::runtime_error("Trailing field registry data");
        std::wstring path=Wide(env,nativePath);
        HMODULE module = LoadLibraryW(path.c_str());
        if (!module) throw std::runtime_error("LoadLibrary failed: " + std::to_string(GetLastError()));
        auto base = reinterpret_cast<unsigned char*>(module);
        auto settingsBegin = *reinterpret_cast<std::uintptr_t*>(base + 0x744f80);
        auto settingsEnd = *reinterpret_cast<std::uintptr_t*>(base + 0x744f88);
        std::printf("[portable] Constructor settings cache bytes=%llu\n", static_cast<unsigned long long>(settingsEnd - settingsBegin)); std::fflush(stdout);
        auto& mappings = *reinterpret_cast<std::unordered_map<std::string, std::string>*>(base + 0x744d50);
        auto head = *reinterpret_cast<void**>(base + 0x744d58);
        if (!head) throw std::runtime_error("Native map constructor did not run");
        mappings.clear();
        for (jsize i = 0; i < count; i++) {
            auto name = static_cast<jstring>(env->GetObjectArrayElement(names, i));
            auto value = static_cast<jstring>(env->GetObjectArrayElement(values, i));
            mappings.emplace(Utf8(env, name), Utf8(env, value));
            env->DeleteLocalRef(name); env->DeleteLocalRef(value);
        }
        auto& methodMappings=*reinterpret_cast<std::unordered_map<std::string,std::string>*>(base+0x744d10);
        methodMappings=mappings;
        std::map<std::string,std::string> settingNames={
            {"(Ljava/lang/String;I)V","BindSetting"},{"(Ljava/lang/String;Z)V","BooleanSetting"},
            {"(Ljava/lang/String;Ljava/awt/Color;)V","ColorSetting"},{"(Ljava/lang/String;FFF)V","FloatSetting"},
            {"(Ljava/lang/String;III)V","IntSetting"},{"(Ljava/lang/String;FFFF)V","MinMaxSetting"},
            {"(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)V","ModeSetting"},
            {"(Ljava/lang/String;[Ljava/lang/String;[Z)V","MultiModeSetting"},
            {"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V","StringSetting"}
        };
        auto& typeIndex=*reinterpret_cast<std::unordered_map<std::string,TypeInfo>*>(base+0x744ea0);
        std::printf("[portable] Constructor type index=%zu\n",typeIndex.size());std::fflush(stdout);
        for(std::size_t i=0;i<typeIndex.size();i++){
            auto at=typeIndex.find(std::to_string(i));if(at==typeIndex.end())throw std::runtime_error("Missing constructor index");
            auto name=settingNames.find(at->second.descriptor);if(name==settingNames.end())throw std::runtime_error("Unknown setting descriptor "+at->second.descriptor);
            auto klass=mappings.find("dev/zprestige/prestige/api/setting/"+name->second);if(klass==mappings.end())throw std::runtime_error("Missing setting class mapping");
            SettingTypes.push_back({static_cast<int>(i),klass->second});
        }
        if(SettingTypes.size()!=9||settingsEnd-settingsBegin!=687*88)throw std::runtime_error("Unexpected settings census");
        for(auto p=settingsBegin;p<settingsEnd;p+=88)SettingKeys.insert(*reinterpret_cast<std::string*>(p));
        Redirect(base+0x15d810,reinterpret_cast<void*>(&LocalModules));
        Redirect(base+0x15e8c0,reinterpret_cast<void*>(&LocalTypes));
        Redirect(base+0x15f540,reinterpret_cast<void*>(&LocalFields));
        for (std::size_t rva : {0x708948, 0x708950, 0x708958, 0x708960}) {
            auto slot = reinterpret_cast<jobject*>(base + rva);
            if (*slot) throw std::runtime_error("Unexpected preinitialized classloader slot");
            *slot = env->NewGlobalRef(loader);
        }
        Loader=env->NewGlobalRef(loader);
        Redirect(base+0xcdc60,reinterpret_cast<void*>(&LocalClass));
        auto runtime = *reinterpret_cast<void***>(base + 0x745910);
        if (!runtime) throw std::runtime_error("Native runtime constructor did not run");
        JavaVM* vm = nullptr;
        if (env->GetJavaVM(&vm) != JNI_OK) throw std::runtime_error("GetJavaVM failed");
        jvmtiEnv* ti = nullptr;
        if (vm->GetEnv(reinterpret_cast<void**>(&ti), JVMTI_VERSION_1_2) != JNI_OK) throw std::runtime_error("JVMTI unavailable");
        runtime[0] = env; runtime[1] = ti; runtime[2] = vm;
        struct Binding { const char* name; const char* descriptor; std::size_t rva; };
        static const Binding bindings[] = {
            {"e", "(ILjava/lang/StringBuilder;)V", 0x16e460},
            {"i", "()V", 0xdb680},
            {"b", "(ILjava/lang/StringBuilder;)V", 0x16e070},
            {"c", "(Ljava/lang/StringBuilder;)V", 0x75200},
            {"h", "()V", 0xdb5e0},
            {"f", "(ILjava/lang/String;Ljava/lang/StringBuilder;)V", 0x16e530},
            {"d", "(Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/StringBuilder;Ljava/lang/String;)V", 0x16e140},
            {"a", "(Ljava/util/List;)V", 0x16e830},
            {"g", "(Ljava/lang/String;)V", 0x172000},
            {"j", "()Z", 0xdb6a0}
        };
        JNINativeMethod methods[10]{};
        for (int i = 0; i < 10; i++) methods[i] = {const_cast<char*>(bindings[i].name), const_cast<char*>(bindings[i].descriptor), base + bindings[i].rva};
        if (env->RegisterNatives(nativeClass, methods, 10) != JNI_OK) throw std::runtime_error("RegisterNatives failed");
        Module = base;
        std::printf("[portable] Native runtime initialized; mappings=%zu; bindings=10\n", mappings.size()); std::fflush(stdout);
    } catch (const std::exception& error) { Fail(env, error.what()); }
}
extern "C" JNIEXPORT void JNICALL Java_bench_prestige_portable_Bridge_initializeUi(JNIEnv* env, jclass) {
    if (!Module) { Fail(env, "Native runtime not initialized"); return; }
    using Initialize = void(*)(JNIEnv*);
    reinterpret_cast<Initialize>(Module + 0xdb6b0)(env);
    if(env->ExceptionCheck()){env->ExceptionDescribe();Fail(env,"Native UI initialization raised an exception");return;}
    auto begin=*reinterpret_cast<std::uintptr_t*>(Module+0x745378);
    auto end=*reinterpret_cast<std::uintptr_t*>(Module+0x745380);
    std::size_t total=0;
    for(auto p=begin;p<end;p+=24){auto first=*reinterpret_cast<std::uintptr_t*>(p);auto last=*reinterpret_cast<std::uintptr_t*>(p+8);auto count=(last-first)/168;total+=count;std::printf("[portable] UI category=%zu modules=%zu\n",(p-begin)/24,count);}
    std::printf("[portable] UI modules total=%zu\n",total);std::fflush(stdout);
}
extern "C" JNIEXPORT void JNICALL Java_bench_prestige_portable_Bridge_toggleUi(JNIEnv* env,jclass type){if(!Module){Fail(env,"Native runtime not initialized");return;}reinterpret_cast<void(*)(JNIEnv*,jclass)>(Module+0xdb680)(env,type);}
extern "C" JNIEXPORT jboolean JNICALL Java_bench_prestige_portable_Bridge_uiVisible(JNIEnv* env,jclass type){if(!Module)return JNI_FALSE;return reinterpret_cast<jboolean(*)(JNIEnv*,jclass)>(Module+0xdb6a0)(env,type);}

