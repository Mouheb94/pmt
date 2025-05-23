# Backend

Ce projet est une API REST développée avec Node.js et Express.

## Prérequis

- Node.js (version 18 ou supérieure)
- npm (généralement installé avec Node.js)
- MongoDB (version 4.4 ou supérieure)

## Installation

1. Cloner le repository
2. Installer les dépendances :
```bash
npm install
```

## Configuration

Créez un fichier `.env` à la racine du projet avec les variables suivantes :
```env
PORT=3000
MONGODB_URI=mongodb://localhost:27017/pmt
JWT_SECRET=votre_secret_jwt
```

## Démarrage du serveur

Pour démarrer le serveur en mode développement :
```bash
npm run dev
```

Pour démarrer le serveur en mode production :
```bash
npm start
```

Le serveur sera accessible à l'adresse `http://localhost:3000`

## Tests

Pour exécuter les tests unitaires :
```bash
npm test
```

Pour exécuter les tests avec couverture :
```bash
npm run test:coverage
```

## Structure du projet

```
backend/
├── src/
│   ├── controllers/    # Contrôleurs de l'API
│   ├── models/        # Modèles de données
│   ├── routes/        # Routes de l'API
│   ├── middleware/    # Middleware personnalisés
│   ├── config/        # Configuration
│   └── utils/         # Utilitaires
├── tests/             # Tests unitaires et d'intégration
└── package.json
```

## API Documentation

La documentation de l'API est disponible à l'adresse `/api-docs` lorsque le serveur est en cours d'exécution.

## Scripts disponibles

- `npm start` : Démarre le serveur en mode production
- `npm run dev` : Démarre le serveur en mode développement avec hot-reload
- `npm test` : Exécute les tests unitaires
- `npm run test:coverage` : Exécute les tests avec rapport de couverture
- `npm run lint` : Vérifie le code avec ESLint
- `npm run build` : Compile le code TypeScript

## Contribution

1. Créez une branche pour votre fonctionnalité (`git checkout -b feature/AmazingFeature`)
2. Committez vos changements (`git commit -m 'Add some AmazingFeature'`)
3. Poussez vers la branche (`git push origin feature/AmazingFeature`)
4. Ouvrez une Pull Request

## Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails. 