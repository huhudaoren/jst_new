const config = {
  devtoolsCliPath: process.env.DEVTOOLS_CLI_PATH || 'C:/Program Files (x86)/Tencent/微信web开发者工具/cli.bat',
  wsEndpoint: process.env.WX_WS_ENDPOINT || '',
  projectPath: process.env.PROJECT_PATH || 'D:/coding/jst_v1/jst-uniapp/unpackage/dist/dev/mp-weixin',
  automatorPort: parseInt(process.env.AUTOMATOR_PORT || '9420'),
  backendUrl: process.env.BACKEND_URL || 'http://127.0.0.1:8080',
  db: {
    host: process.env.DB_HOST || '59.110.53.165',
    port: parseInt(process.env.DB_PORT || '3306'),
    database: process.env.DB_NAME || 'jst_new',
    user: process.env.DB_USER || 'jst_new',
    password: process.env.DB_PASS || 'J8zZpAa4zG8y6a7e',
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
