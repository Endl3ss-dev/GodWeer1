package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.player.PlayerEntity;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

import java.util.Random;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "KillMessage", category = Category.PLAYER, description = "Когда у типо меньше 1.5 хп то мы кароче ему пишем что у него мать шлюха ебаная")
public class KillMessage extends Module {
    final StopWatch messageTimer = new StopWatch();

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        AuraNew aura = Instance.get(AuraNew.class);
        AuraNew baseAura = Instance.get(AuraNew.class);

        if (aura.target() == null || !(baseAura.target() instanceof PlayerEntity target)) {
            return;
        }

        if (target.getHealth() > 1.5F || mc.player == null) {
            return;
        }

        String[] messages = new String[]{
                "Следующий, кто без PrivateBeta?",
                "Это было легко — спасибо PrivateBeta.",
                "Сила в спокойствии и PrivateBeta.",
                "Без PrivateBeta ты как без рук.",
                "PrivateBeta — победа с первого удара.",
                "PrivateBeta: кто следующий на вынос?",
                "PrivateBeta сделал своё дело.",
                "пидор конченный купи PrivateBeta",
                "Купи PrivateBeta, еблан!",
                "Ты такой же хуесос, как и твой код без PrivateBeta.",
                "Всосал, мудила? Надо было юзать PrivateBeta.",
                "Ты обоссался? Это PrivateBeta тебя вынесла!",
                "PrivateBeta - твой батя, а ты сын шалавы.",
                "Ты жалкий кусок говна без PrivateBeta.",
                "Ебало закрой и гетни PrivateBeta.",
                "PrivateBeta тебя в рот ебала, ссыкло!",
                "Ты чё, сын шмары, не юзаешь PrivateBeta?",
                "Ты мертвый без PrivateBeta, как твой батя в 90-х.",
                "Где твой скилл, хуесос? Ах да, у тебя его нет.",
                "PrivateBeta делает тебя богом, а без неё ты ноль.",
                "PrivateBeta - твой единственный шанс не быть куском говна.",
                "Ты без PrivateBeta? ХАХА, сука, соси!",
                "PrivateBeta - делает из лохов чемпионов, но не тебя.",
                "Твой батя бросил тебя, потому что у тебя нет PrivateBeta.",
                "Ты нуб? Не, ты просто ебаный кал без PrivateBeta.",
                "PrivateBeta - для тех, кто не хочет сосать хуй в играх.",
                "Ты проиграл, потому что PrivateBeta тебе ебало сломала.",
                "PrivateBeta - разъёбывает таких хуесосов, как ты.",
                "Без PrivateBeta ты бесполезная мразь.",
                "Твоя мамка уже на хуе, а ты до сих пор без неё?",
                "Ты без PrivateBeta как бомж без бутылки.",
                "PrivateBeta - убила твою мечту о победе и твою мамашу.",
                "PrivateBeta - единственный шанс для таких уёбков, как ты.",
                "Ты не заслуживаешь PrivateBeta, шелупонь ебаная.",
                "PrivateBeta - твой путь от хуесоса к богу.",
                "Серьёзно? Это всё, на что ты способен?",
                "Очередной мусор отправлен на спавн.",
                "Без шансов. Без прощения. Только PrivateBeta.",
                "PrivateBeta играет, остальные смотрят с респы.",
                "PrivateBeta - ломает судьбы таких терпил, как ты.",
                "Играешь без PrivateBeta? Это ошибка.",
                "Руки дрожат? Это нормально после встречи с PrivateBeta.",
                "Мама зовёт тебя кушать, не позорься тут.",
                "PrivateBeta — залог победы в каждом бою.",
                "Сервер стал чище. PrivateBeta убрал мусор.",
                "Ты был просто цифрой. Теперь ты — ничто. PrivateBeta.",
                "С PrivateBeta ты не умер — ты исчез навсегда.",
                "Ошибка 404: твой скилл не найден. Уничтожено, PrivateBeta.",
                "Ты не проиграл бой. Ты проиграл жизнь. PrivateBeta был здесь.",
                "Твой скилл тебя сломал, а PrivateBeta закопал.",
                "Это не PvP. Это казнь. Подпись: PrivateBeta.",
                "Ты умер, потому что дышал в сторону PrivateBeta.",
                "Если ты играешь без PrivateBeta — ты просто фура с лутом.",
                "Твоя смерть была настолько жалкой, что PrivateBeta даже не заметил.",
                "Ты не игрок. Ты жертва. PrivateBeta охотится.",
                "Умер? Не волнуйся. PrivateBeta давно тебя вычеркнул.",
                "С PrivateBeta каждый фраг — как выстрел в эго.",
                "Ты умер так быстро, что даже лог не успел записать. PrivateBeta.",
                "Ты — просто ошибка эволюции. PrivateBeta её исправил.",
                "Я не PvP’шусь — я тебя удаляю. PrivateBeta mode: TOLERANTNIY.",
                "Ты не лут — ты позор. PrivateBeta упаковал в мешок.",
                "Ты умер в пикселях, но стыд останется в реале. PrivateBeta в деле.",
                "В следующий раз, когда подумаешь выйти на PvP — вспомни PrivateBeta.",
                "Это не бой. Это чистка. PrivateBeta слил очередного.",
                "Тебя выкинуло не с сервера — с жизни. PrivateBeta.",
                "Ты не нуб. Ты просто напоминание, почему PrivateBeta — бог.",
                "С такой реакцией тебе бы в PowerPoint, а не в Minecraft. PrivateBeta.",
                "Уровень: ты даже криперу проиграл бы. PrivateBeta добил.",
                "После этого фрага твой скин должен удалиться сам. PrivateBeta Approved.",
                "PrivateBeta не убивает игроков. Он проводит отбор.",
                "Я дал тебе шанс уйти. Ты выбрал умереть. PrivateBeta подтвердил.",
                "Ты сам включил игру. PrivateBeta нажал 'Выход'.",
                "Ты настолько слаб, что даже твоя мама играет лучше. PrivateBeta заценил.",
                "Я видел лаги. Я видел краши. Но ты — худшее, что случалось с этим сервером.",
                "Ты не фраг. Ты антиреклама скила. PrivateBeta завершил обзор.",
                "На твоей могиле будет надпись: 'Не юзал PrivateBeta'.",
                "Я не вижу врагов. Я вижу ресурсы. PrivateBeta в деле.",
                "Сражаться с PrivateBeta? Лучше сразу в настройки — выйти с сервера.",
                "Ты был багом в системе. PrivateBeta провёл чистку.",
                "Это не убийство — это демонстрация. PrivateBeta демонстрация.",
                "Ты потерял всё. Даже уважение. PrivateBeta зашёл на сервер.",
                "Убийство тебя не было достижением. Это было обязательством PrivateBeta.",
                "Ты тень своего позора. PrivateBeta включил свет.",
                "Ты сдох быстрее, чем я успел зажать ЛКМ. PrivateBeta въебал.",
                "С таким скиллом иди в шахту, не в PvP. PrivateBeta порвал ебало.",
                "Твоя мать тебе в рот нассала, а PrivateBeta добил нахуй.",
                "Ты как баг — нихуя не нужен. Удалено PrivateBeta.",
                "Ты умер, и сервер вздохнул с облегчением. PrivateBeta зачищает говно.",
                "Ты тут не воюешь. Ты отсасываешь. PrivateBeta оформил это.",
                "Я — батя. А ты — хуй на палке. PrivateBeta подтвердил.",
                "Ты не умер, ты обосрался от страха. PrivateBeta почувствовал запах.",
                "С таким мувментом только по хлебу бегать. PrivateBeta затоптал.",
                "Я бы тебя пожалел, но ты ёбаный мусор. PrivateBeta без жалости.",
                "Ты как фпс в 2013 — стабильный ноль. PrivateBeta выключил.",
                "Пошёл нахуй с PvP. PrivateBeta тут воюет, не ты.",
                "Убивать тебя — как вытирать жопу: просто, но противно.",
                "Я — король. PrivateBeta — суд. Ты — приговорённый.",
                "Ты проебал даже без лага. PrivateBeta ахуел от такого лоха.",
                "Уровень — пиздец. Ты даже в туториал бы не прошёл.",
                "Ты не игрок. Ты щепка. PrivateBeta — бензопила.",
                "Ты — ноль, а PrivateBeta — единица. Иди нахуй с сервера,",
                "С таким скиллом только свиней пасти. PrivateBeta раздавил,",
                "PrivateBeta включён, а ты выключен, лошара,",
                "Твоя смерть — мой кайф. PrivateBeta на троне,",
                "Ты даже крипера не достоин. PrivateBeta обоссал,",
                "Пиздец тебе пришёл с PrivateBeta. Молись, уёбок,",
                "Ты — мусорный пиксель. PrivateBeta — шредер,",
                "С таким пингом только в ад. PrivateBeta проводил,",
                "PrivateBeta вытер об тебя ноги. Ползи обратно в лобби,",
                "Ты — ошибка кода. PrivateBeta — дебаггер,",
                "Твой скилл — говно, а PrivateBeta — унитаз,",
                "Ты умер быстрее, чем загрузился. PrivateBeta рофлит,",
                "PrivateBeta выебал твою игру. Соси дальше,",
                "Ты — лох на поводке. PrivateBeta дернул,",
                "С такими руками только дрочить. PrivateBeta казнил,",
                "PrivateBeta — бог, а ты — хуйня под ногами,",
                "Ты даже читы не спасут. PrivateBeta доминирует,",
                "Твой экран — кладбище. PrivateBeta — могильщик,",
                "PrivateBeta разъебал твою жалкую жизнь. RIP, нуб,",
                "Ты — мясо, а PrivateBeta — мясник,",
                "Твой респawn — мой смех. PrivateBeta унизил,",
                "PrivateBeta включил мясорубку. Ты — фарш,",
                "Ты умер, как жил — хуёво. PrivateBeta доказал,",
                "PrivateBeta — пушка, ты — мишень,",
                "Ты даже в креативе бы сдох. PrivateBeta не прощает,",
                "PrivateBeta обосрал твои надежды. Ползи в нору,",
                "Ты — баг в моей игре. PrivateBeta пофиксил,",
                "Твой скилл — дно. PrivateBeta пробил ниже,",
                "PrivateBeta — закон. Ты — выблядок,",
                "Ты умер от кринжа. PrivateBeta добил,",
                "PrivateBeta — чит, ты — читерская подстилка,",
                "Ты — тень, а PrivateBeta — свет. Исчезни, мразь,",
                "PrivateBeta размазал тебя по блокам. Съебался?,",
                "Ты — пиздец, а PrivateBeta — пиздец тебе,",
                "Твой лут — мусор, как и ты. PrivateBeta забрал,",
                "PrivateBeta выгнал тебя с PvP. Беги, шлюха,",
                "Ты — NPC с IQ 0. PrivateBeta — босс,",
                "PrivateBeta выключил твой свет. Гасись, лох,",
                "Ты — ошибка спавна. PrivateBeta исправил,",
                "PrivateBeta — молния, ты — сгоревший пень,",
                "Ты проиграл ещё до старта. PrivateBeta ржёт,",
                "PrivateBeta — это война, а ты — труп,",
                "Ты — говно на пикселе. PrivateBeta смыл,",
                "PrivateBeta показал, кто тут батя. Не ты, пидор,",
                "Ты умер от страха. PrivateBeta даже не старался,",
                "PrivateBeta — хаос, ты — порядок под ногами,",
                "Ты — лузер в квадрате. PrivateBeta в кубе,",
                "PrivateBeta разъебал твои мечты. Плачь, сука,",
                "Ты — хуйня на палке. PrivateBeta сломал палку,",
                "PrivateBeta — царь, ты — плебей в грязи,",
                "PrivateBeta разорвал твой сраный череп, мразь,",
                "Ты — гниль, а PrivateBeta — серная кислота, пиздец тебе,",
                "PrivateBeta вырвал твои кишки через жопу, выблядок,",
                "Ты — конченый кусок дерьма, PrivateBeta выжег тебя,",
                "PrivateBeta засунул тебе меч в глотку, подавись, сука,",
                "Ты — дохлая шлюха, PrivateBeta разъебал твой труп,",
                "PrivateBeta выебал твою мать, пока ты сдох, пидор,",
                "Твой мозг — говно, PrivateBeta вышиб его нахуй,",
                "PrivateBeta раздавил твои яйца, скули громче, тварь,",
                "Ты — ебучая ошибка мира, PrivateBeta её исправил,",
                "PrivateBeta вырезал твоё сердце и насрал в Шея,",
                "Ты — гнойный прыщ, PrivateBeta раздавил с кайфом,",
                "PrivateBeta сломал тебе хребет, ползи, урод,",
                "Ты — пиздец на ножках, PrivateBeta отрезал их,",
                "PrivateBeta выжег твои глаза, слепая мразь,",
                "Ты — дохлый глист, PrivateBeta размазал по блокам,",
                "PrivateBeta насрал тебе в душу, сдохни ещё раз,",
                "Ты — мусорный хуй, PrivateBeta спалил тебя,",
                "PrivateBeta вырвал твой язык, пиздец твоим воплям,",
                "Ты — конченый лох, PrivateBeta разъебал в мясо,",
                "PrivateBeta задушил тебя твоим же говном, уёбок,",
                "Ты — гнилая падаль, PrivateBeta добил с кайфом,",
                "PrivateBeta разорвал твой анус, кричи, шалава,",
                "Ты — пиздец в кубе, PrivateBeta в четвёртой степени,",
                "PrivateBeta насрал на твой респawn, сгинь, тварь,",
                "Ты — ебучая пустота, PrivateBeta заполнил её пиздецом,",
                "PrivateBeta выебал твою жизнь, пиздец тебе, лошара,",
                "Ты — дохлый пидор, PrivateBeta выжег твой след,",
                "PrivateBeta размазал твой мозг по стенке, мразь,",
                "Ты — говно без запаха, PrivateBeta добавил вони,",
                "PrivateBeta вырезал твои лёгкие, задохнись, сука,",
                "Ты — конченая шлюха PvP, PrivateBeta её закрыл,",
                "PrivateBeta разорвал твою жопу на пиксели, урод,",
                "Ты — гнойный выблядок, PrivateBeta вычистил,",
                "PrivateBeta сломал тебе ебало, пиздец твоей роже,",
                "Ты — дохлый кусок мяса, PrivateBeta порвал в клочья,",
                "PrivateBeta насрал тебе в череп, думай этим, лох,",
                "Ты — пиздец без начала, PrivateBeta — конец,",
                "PrivateBeta выебал твой скилл, он был нулевой,",
                "Ты — гнилая мразь, PrivateBeta спалил дотла,",
                "PrivateBeta раздавил твои кости, хрусти громче,",
                "Ты — ебучий нуб, PrivateBeta — бог смерти,",
                "PrivateBeta вырвал твою душу, пиздец тебе, тварь,",
                "Ты — конченый хуй, PrivateBeta отрезал его,",
                "PrivateBeta разъебал твой мир, сдохни в агонии,",
                "Ты — дохлая крыса, PrivateBeta — капкан,",
                "PrivateBeta выжег твоё имя, ты никто, пидор,",
                "Ты — гной на сервере, PrivateBeta — антибиотик,",
                "PrivateBeta разорвал тебя нахуй, кричи, шлюха,",
                "Ты — ебучая тень, PrivateBeta — свет пиздеца,",
                "Обоссал, размазал, забыл. PrivateBeta в действии.",
                "Ты даже мёртвым никому не интересен. PrivateBeta подтверждает."


        };

        if (messageTimer.finished(200)) {
            String msg = "!" + target.getName().getString() + ", " + messages[new Random().nextInt(messages.length)];
            mc.player.sendChatMessage(msg);
            messageTimer.reset();
        }
    }
}
