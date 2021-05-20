module.exports = {
  /**
     * Application configuration section
     * http://pm2.keymetrics.io/docs/usage/application-declaration/
     */
  apps: [
    // First application
    {
      name: 'wk-admin',
      script: 'npm run start'
    }
  ]
}
