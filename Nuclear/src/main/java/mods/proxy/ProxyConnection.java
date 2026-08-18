package mods.proxy;

import java.io.IOException;
import java.net.*;
import java.util.prefs.Preferences;

public class ProxyConnection {
    private ProxyType proxyType;
    private String host;
    private int port;
    private String username;
    private String password;
    private boolean enabled;
    private String lastError;
    private static ProxyAuthenticator authenticator = null;

    public ProxyConnection() {
        this.proxyType = ProxyType.DIRECT;
        this.host = "";
        this.port = 0;
        this.username = "";
        this.password = "";
        this.enabled = false;
        this.lastError = null;
        this.loadSettings();
    }

    public boolean setupProxy(ProxyType type, String host, int port, String username, String password) {
        try {
            this.proxyType = type;
            this.host = host != null ? host.trim() : "";
            this.port = port;
            this.username = username != null ? username.trim() : "";
            this.password = password != null ? password.trim() : "";
            this.lastError = null;
            if (type == ProxyType.DIRECT) {
                this.enabled = false;
                this.clearSystemProperties();
            } else {
                this.enabled = true;
                this.setSystemProperties();
            }

            this.saveSettings();
            return true;
        } catch (Exception e) {
            this.lastError = "Ошибка настройки прокси: " + e.getMessage();
            return false;
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled && this.proxyType != ProxyType.DIRECT;
        if (this.enabled) {
            this.setSystemProperties();
        } else {
            this.clearSystemProperties();
        }

        this.saveSettings();
    }

    public Proxy getJavaProxy() {
        if (this.enabled && this.proxyType != ProxyType.DIRECT && !this.host.isEmpty() && this.port > 0) {
            try {
                InetSocketAddress address = new InetSocketAddress(this.host, this.port);
                Proxy var10000;
                switch (this.proxyType) {
                    case HTTP:
                        var10000 = new Proxy(Proxy.Type.HTTP, address);
                        break;
                    case SOCKS4:
                    case SOCKS5:
                        var10000 = new Proxy(Proxy.Type.SOCKS, address);
                        break;
                    default:
                        var10000 = Proxy.NO_PROXY;
                }

                return var10000;
            } catch (Exception e) {
                this.lastError = "Ошибка создания прокси: " + e.getMessage();
                return Proxy.NO_PROXY;
            }
        } else {
            return Proxy.NO_PROXY;
        }
    }

    private void setSystemProperties() {
        this.clearSystemProperties();
        if (this.hasAuth()) {
            try {
                if (this.proxyType != ProxyType.SOCKS4 && this.proxyType != ProxyType.SOCKS5) {
                    if (this.proxyType == ProxyType.HTTP) {
                        if (authenticator == null) {
                            authenticator = new ProxyAuthenticator();
                            Authenticator.setDefault(authenticator);
                        }

                        authenticator.setCredentials(this.username, this.password);
                    }
                } else {
                    System.setProperty("java.net.socks.username", this.username);
                    System.setProperty("java.net.socks.password", this.password);
                }
            } catch (Exception e) {
                this.lastError = "Ошибка настройки системных свойств: " + e.getMessage();
            }

        }
    }

    private void clearSystemProperties() {
        System.clearProperty("java.net.socks.username");
        System.clearProperty("java.net.socks.password");
        if (authenticator != null) {
            authenticator.clearCredentials();
        }

    }

    private boolean hasAuth() {
        return this.proxyType.supportsAuth() && !this.username.isEmpty() && !this.password.isEmpty();
    }

    public boolean testConnection() {
        if (this.proxyType == ProxyType.DIRECT) {
            return true;
        } else if (!this.host.isEmpty() && this.port > 0) {
            try {
                this.setSystemProperties();
                Proxy proxy = this.getJavaProxy();
                if (proxy == Proxy.NO_PROXY) {
                    this.lastError = "Не удалось создать прокси";
                    return false;
                } else {
                    URL testUrl = new URL("http://www.google.com");
                    URLConnection connection = testUrl.openConnection(proxy);
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    if (this.proxyType == ProxyType.HTTP) {
                        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                    }

                    connection.connect();
                    connection.getInputStream().close();
                    this.lastError = null;
                    return true;
                }
            } catch (ConnectException var4) {
                this.lastError = "Не удалось подключиться к серверу";
                return false;
            } catch (SocketTimeoutException var5) {
                this.lastError = "Превышен тайм-аут запроса";
                return false;
            } catch (IOException e) {
                String message = e.getMessage().toLowerCase();
                if (!message.contains("407") && !message.contains("authentication") && !message.contains("unauthorized")) {
                    if (message.contains("connection refused")) {
                        this.lastError = "Не удалось подключиться к серверу";
                    } else {
                        this.lastError = "Ошибка тестирования: " + e.getMessage();
                    }
                } else {
                    this.lastError = "Ошибка аутентификации: неверные учетные данные или пароль";
                }

                return false;
            } catch (Exception e) {
                this.lastError = "Ошибка тестирования: " + e.getMessage();
                return false;
            }
        } else {
            this.lastError = "Не указан хост или порт для тестирования";
            return false;
        }
    }

    public String getProxyString() {
        if (this.proxyType != ProxyType.DIRECT && this.enabled) {
            if (!this.host.isEmpty() && this.port > 0) {
                String var10000 = this.proxyType.getPrefix();
                return var10000 + this.host + ":" + this.port;
            } else {
                return "Не указан хост или порт";
            }
        } else {
            return "Не используется прокси";
        }
    }

    public void reset() {
        this.proxyType = ProxyType.DIRECT;
        this.host = "";
        this.port = 0;
        this.username = "";
        this.password = "";
        this.enabled = false;
        this.lastError = null;
        this.clearSystemProperties();
        this.saveSettings();
    }

    public boolean validateSettings() {
        if (this.proxyType == ProxyType.DIRECT) {
            return true;
        } else if (this.host != null && !this.host.trim().isEmpty()) {
            if (this.port > 0 && this.port <= 65535) {
                if (this.proxyType.supportsAuth()) {
                    if (!this.username.isEmpty() && this.password.isEmpty()) {
                        this.lastError = "Не указан логин, нужно установить пароль";
                        return false;
                    }
                } else if (this.proxyType == ProxyType.SOCKS4 && (!this.username.isEmpty() || !this.password.isEmpty())) {
                    this.lastError = "SOCKS4 не поддерживает аутентификацию";
                    return false;
                }

                return true;
            } else {
                this.lastError = "Неверный порт (должен быть от 1 до 65535)";
                return false;
            }
        } else {
            this.lastError = "Не указан хост";
            return false;
        }
    }

    private void saveSettings() {
        try {
            Preferences prefs = Preferences.userRoot().node("C:\\Nuclear\\game\\Nuclear\\proxy");
            prefs.put("proxyType", this.proxyType.name());
            prefs.put("host", this.host);
            prefs.putInt("port", this.port);
            prefs.put("username", this.username);
            prefs.put("password", this.password);
            prefs.putBoolean("enabled", this.enabled);
            prefs.flush();
        } catch (Exception e) {
            System.err.println("Ошибка сохранения настроек: " + e.getMessage());
            this.lastError = "Не удалось сохранить настройки";
        }

    }

    private void loadSettings() {
        try {
            Preferences prefs = Preferences.userRoot().node("C:\\Nuclear\\game\\Nuclear\\proxy");
            String typeStr = prefs.get("proxyType", ProxyType.DIRECT.name());

            try {
                this.proxyType = ProxyType.valueOf(typeStr);
            } catch (Exception var4) {
                this.proxyType = ProxyType.DIRECT;
                System.err.println("Неизвестный тип прокси: " + typeStr);
            }

            this.host = prefs.get("host", "");
            this.port = prefs.getInt("port", 0);
            this.username = prefs.get("username", "");
            this.password = prefs.get("password", "");
            this.enabled = prefs.getBoolean("enabled", false) && this.proxyType != ProxyType.DIRECT;
            if (!this.validateSettings()) {
                System.err.println("Недействительные настройки: " + this.lastError);
                this.reset();
                return;
            }

            if (this.enabled) {
                this.setSystemProperties();
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки настроек: " + e.getMessage());
            this.reset();
        }

    }

    public ProxyType getProxyType() {
        return this.proxyType;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getLastError() {
        return this.lastError;
    }

    private static class ProxyAuthenticator extends Authenticator {
        private String username = "";
        private String password = "";

        public void setCredentials(String username, String password) {
            this.username = username != null ? username : "";
            this.password = password != null ? password : "";
        }

        public void clearCredentials() {
            this.username = "";
            this.password = "";
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return this.getRequestorType() == RequestorType.PROXY && !this.username.isEmpty() ? new PasswordAuthentication(this.username, this.password.toCharArray()) : null;
        }
    }
}

