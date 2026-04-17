Before running the application, make sure you have the following installed:

Java JDK 17 or higher — Download here
JavaFX SDK 21 — Download here
IntelliJ IDEA (Community or Ultimate) — Download here


🚀 How to Run
Follow these steps carefully:
Step 1 — Download JavaFX SDK

Go to https://gluonhq.com/products/javafx/
Download JavaFX 21 LTS, choose Windows, x64, SDK
Extract the downloaded zip file somewhere easy to find, for example:

   C:\Users\YourName\Desktop\javafx-sdk-21.0.10
Step 2 — Open the Project in IntelliJ

Download or clone this repository
Open IntelliJ IDEA
Click File → Open and select the HealthSystem folder (the outer one)
Right-click the src folder → Mark Directory As → Sources Root

Step 3 — Add JavaFX as a Library

Go to File → Project Structure → Libraries
Click the + button → Java
Navigate to your JavaFX SDK folder and select the lib folder inside it
Click OK and Apply

Step 4 — Configure Run Settings

Go to Run → Edit Configurations
Click + → Application
Set Main class to: healthsystem.ui.MainApp
Click Modify options → tick Add VM options
In the VM options field, paste this (replace the path with your actual JavaFX lib path):

   --module-path "C:\Users\YourName\Desktop\javafx-sdk-21.0.10\lib" --add-modules javafx.controls,javafx.fxml

Click Apply then OK

Step 5 — Run the App
Click the green ▶ Run button. The application should open.

Note: A data/ folder will be created automatically in your project directory the first time you run the app. This is where all CSV data files are stored.

