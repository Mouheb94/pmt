const { defineConfig } = require("cypress");

module.exports = defineConfig({
  e2e: {
    chromeWebSecurity: false, 
    //baseUrl: 'http://localhost:4200',
    setupNodeEvents(on, config) {
      // Vos plugins personnalisés (si nécessaire)
      return config;
    },
  },
});
