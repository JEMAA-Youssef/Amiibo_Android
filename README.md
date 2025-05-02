#  Amiibo Quiz Android App

Une application Android développée en Kotlin qui permet aux utilisateurs de tester leurs connaissances sur les figurines **Amiibo** à travers un quizz interactif avec images.

##  Description

L'application utilise l'API [AmiiboAPI](https://www.amiiboapi.com) pour récupérer dynamiquement les **GameSeries** et les **Amiibos**.  
L'utilisateur sélectionne au minimum 4 séries de jeux et peut alors lancer une session de quiz, où il doit identifier les personnages ou leurs séries à partir d'une image affichée.

##  Fonctionnalités

-  Récupération des GameSeries et Amiibo via **Retrofit** (API REST)
-  Sélection multiple de séries et lancement dynamique du quiz
-  Interface moderne et responsive en `ConstraintLayout`
-  Affichage d’un score évolutif avec couleur dynamique (vert/rouge)
-  Feedback visuel (boutons colorés selon bonne/mauvaise réponse)
-  Gestes de swipe pour passer une question (avec pénalité)
-  Système de musique de fond :
- Lecture continue
- Pause automatique si l'app passe en arrière-plan
-  Stockage local des Amiibos sélectionnés avec **Realm**
-  Navigation fluide entre les activités
-  Page de félicitations à la fin du quiz 🎉
-  Animations et effets de transition


##  Lancement du projet

```bash
git clone https://github.com/JEMAA-Youssef/Amiibo_Android.git
