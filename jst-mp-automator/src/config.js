const config = {
  devtoolsCliPath: process.env.DEVTOOLS_CLI_PATH || 'D:\\wxdevtools-auto\\cli.bat',
  wsEndpoint: process.env.WX_WS_ENDPOINT || '',
  projectPath: process.env.PROJECT_PATH || 'D:/coding/jst_v1/jst-uniapp/unpackage/dist/dev/mp-weixin',
  automatorPort: parseInt(process.env.AUTOMATOR_PORT || '9420'),
  backendUrl: process.env.BACKEND_URL || 'http://127.0.0.1:8080',
  db: {
    host: process.env.DB_HOST || '127.0.0.1',
    port: parseInt(process.env.DB_PORT || '3306'),
    database: process.env.DB_NAME || 'jst',
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASS || '123456',
    waitForConnections: true,
    connectionLimit: 5,
    charset: 'utf8mb4'
  },
  actionTimeout: parseInt(process.env.ACTION_TIMEOUT || '10000'),
  retryCount: parseInt(process.env.RETRY_COUNT || '3'),
  retryBaseDelay: 2000,
  waitAfterNavigate: 2000,
  waitBeforeScreenshot: 1000,
  flowsDir: new URL('../flows', import.meta.url).pathname.replace(/^\/([A-Z]:)/, '$1'),
  reportsDir: new URL('../reports', import.meta.url).pathname.replace(/^\/([A-Z]:)/, '$1'),
  screenshotsDir: new URL('../screenshots', import.meta.url).pathname.replace(/^\/([A-Z]:)/, '$1'),
};

export default config;
