# Smart Home Automation Simulator
## Complete Project Deployment and Distribution Guide
**Domain:** Java Desktop Application Deployment & Distribution  
**Target Platform:** Windows 10/11, macOS, Linux  
**Target Environment:** Standalone Executable JAR / Windows Batch Launcher / Native App  

---

## 1. Deployment Overview

Deploying a Java desktop application involves compiling the source code, bundling the bytecode and manifest into a standalone **Executable JAR file (`SmartHomeSimulator.jar`)**, and configuring one-click launchers so that examiners, professors, or users can run the application with a single click.

This guide provides step-by-step instructions for:
- **Method 1:** Automated 1-Click Deployment using pre-configured batch scripts.
- **Method 2:** Manual Command-Line Compilation & JAR Packaging.
- **Method 3:** Exporting Runnable JAR from IDEs (IntelliJ IDEA, Eclipse, NetBeans, VS Code).
- **Method 4:** Creating a standalone Windows `.exe` (via Launch4j / `jpackage`).
- **Method 5:** Preparing the final Submission ZIP package for university evaluation.

---

## 2. Prerequisites & Java Environment Setup

### 2.1 Verify Java Installation
Open **Command Prompt (CMD)** or **PowerShell** and verify that Java is installed:
```powershell
java -version
javac -version
```
> **Requirement:** JDK 17 (LTS) or JDK 21 (LTS) is recommended. (JDK 8 through 22 are fully supported).

### 2.2 If Java is Not Installed or Not in PATH:
1. Download OpenJDK from Adoptium (Eclipse Temurin):  
   🔗 **[https://adoptium.net/](https://adoptium.net/)** (Select **JDK 17** or **JDK 21** - Windows `.msi` installer).
2. During installation, make sure to check:
   - ✅ **Set JAVA_HOME variable**
   - ✅ **Add to PATH**
3. Restart your command prompt to apply changes.

---

## 3. Method 1: Automated 1-Click Deployment (Recommended)

Three automated helper scripts have been generated in your project root:

| Script File | Purpose | How to Use |
| :--- | :--- | :--- |
| **`build_and_run.bat`** | Compiles all Java packages, builds `SmartHomeSimulator.jar`, and launches the app. | **Double-click** `build_and_run.bat` |
| **`run.bat`** | Instantly launches the pre-built application in the background using `javaw`. | **Double-click** `run.bat` |
| **`run_tests.bat`** | Runs the 21-point automated SQA unit and integration test suite in console. | **Double-click** `run_tests.bat` |

---

## 4. Method 2: Manual Command-Line Packaging (CMD / PowerShell)

If you prefer building and packaging manually via terminal:

### Step 1: Open Terminal in Project Directory
```powershell
cd "c:\Users\hp\Downloads\Home automation project"
```

### Step 2: Create Output Folder
```powershell
mkdir bin
```

### Step 3: Compile All Java Packages
```powershell
javac -d bin -sourcepath src src/com/smarthome/interfaces/*.java src/com/smarthome/exceptions/*.java src/com/smarthome/model/*.java src/com/smarthome/automation/*.java src/com/smarthome/gui/*.java src/com/smarthome/test/*.java src/com/smarthome/*.java
```

### Step 4: Verify or Create Manifest File
Ensure `manifest.txt` exists in the project root with the following content:
```text
Manifest-Version: 1.0
Main-Class: com.smarthome.SmartHomeApp

```
*(Note: Ensure there is an empty newline at the end of `manifest.txt`)*

### Step 5: Package into Executable JAR
```powershell
jar cvfm SmartHomeSimulator.jar manifest.txt -C bin .
```

### Step 6: Run the Packaged JAR
```powershell
# Using java (with console output):
java -jar SmartHomeSimulator.jar

# Or using javaw (pure windowed mode without console window):
javaw -jar SmartHomeSimulator.jar
```

---

## 5. Method 3: Exporting from Major Java IDEs

If you or your professors use an IDE, follow the relevant guide below:

### 5.1 IntelliJ IDEA
1. Open the project folder in IntelliJ IDEA.
2. Go to **File** ➔ **Project Structure** (or press `Ctrl + Alt + Shift + S`).
3. Under **Project Settings**, select **Artifacts**.
4. Click the green **`+`** button ➔ Select **JAR** ➔ **From modules with dependencies...**
5. In the dialog:
   - **Main Class:** Click browse and select `com.smarthome.SmartHomeApp`.
   - **JAR files from libraries:** Select *extract to the target JAR*.
   - Click **OK**.
6. Check the checkbox **Build on make** (optional) and click **Apply** ➔ **OK**.
7. In the top menu, go to **Build** ➔ **Build Artifacts...** ➔ Select your artifact ➔ Click **Build**.
8. Your executable `.jar` will be generated inside the `out/artifacts/` folder.

---

### 5.2 Eclipse IDE
1. Open Eclipse and import the project: **File** ➔ **Open Projects from File System...**
2. Ensure the project builds without errors. Run `SmartHomeApp.java` at least once so Eclipse creates a launch configuration.
3. Right-click on the project in *Package Explorer* ➔ Click **Export...**
4. Expand **Java** folder ➔ Select **Runnable JAR file** ➔ Click **Next**.
5. In the configuration window:
   - **Launch configuration:** Select `SmartHomeApp - Home automation project`.
   - **Export destination:** Browse and choose `c:\Users\hp\Downloads\Home automation project\SmartHomeSimulator.jar`.
   - **Library handling:** Select *Extract required libraries into generated JAR*.
6. Click **Finish**. You can now double-click the generated `.jar` to run it.

---

### 5.3 Apache NetBeans
1. Open NetBeans and open the project.
2. Right-click project ➔ **Properties**.
3. Under **Categories**, select **Run**.
4. In **Main Class**, click Browse and select `com.smarthome.SmartHomeApp`. Click **OK**.
5. In the top toolbar, click the **Clean and Build** icon (Hammer and Broom).
6. NetBeans will create a `dist/` folder containing `Home_automation_project.jar`.

---

### 5.4 Visual Studio Code (VS Code)
1. Install the official **Extension Pack for Java** (by Microsoft).
2. Open the project folder in VS Code.
3. In the left activity bar, open the **Java Projects** view (bottom left).
4. Hover over your project name, click the **`...`** (More Actions) icon ➔ Select **Export Jar...**
5. Select `com.smarthome.SmartHomeApp` as the Main Class.
6. Choose the output destination.

---

## 6. Method 4: Packaging as a Native Windows `.exe`

To allow users without Java to run your simulator, or to create a professional desktop shortcut:

### Option A: Using Launch4j (Free & Popular for College Projects)
1. Download and install **Launch4j** (free from [launch4j.sourceforge.net](https://launch4j.sourceforge.net/)).
2. In the **Basic** tab:
   - **Output file:** `c:\Users\hp\Downloads\Home automation project\SmartHomeSimulator.exe`
   - **Jar:** `c:\Users\hp\Downloads\Home automation project\SmartHomeSimulator.jar`
   - **Icon (optional):** Select any `.ico` icon file.
3. In the **JRE** tab:
   - **Min JRE version:** `1.8.0` (or `17.0.0`).
4. Click the **Build Wrapper** icon (Gear icon at the top).
5. A standalone `SmartHomeSimulator.exe` is created!

---

### Option B: Using JDK `jpackage` (Built into JDK 14+)
If you have JDK 17 or 21 installed, you can create a self-contained Windows application bundle containing an embedded JRE:
```powershell
jpackage --type app-image `
  --input . `
  --name "SmartHomeSimulator" `
  --main-jar "SmartHomeSimulator.jar" `
  --main-class "com.smarthome.SmartHomeApp" `
  --dest "dist"
```
This produces a `dist/SmartHomeSimulator/` folder containing `SmartHomeSimulator.exe` along with its own private Java runtime. It can be copied to any Windows computer and executed without installing Java!

---

## 7. Method 5: Final Submission ZIP Package for University Evaluation

For formal college submission (CD/DVD, USB drive, or Google Classroom / LMS upload), organize your files as follows:

```
Smart_Home_Automation_Simulator_Final_Submission/
│
├── 🚀 RUN_APPLICATION/
│   ├── SmartHomeSimulator.jar          // Standalone double-clickable JAR
│   ├── run.bat                          // 1-Click Windows launcher
│   └── run_tests.bat                    // 1-Click SQA automated test suite
│
├── 📖 DOCUMENTATION/
│   ├── Phase_1_Requirement_Analysis_and_System_Design.md
│   ├── Phase_2_Core_OOP_Implementation_and_Device_Simulation.md
│   ├── Phase_3_GUI_Development_and_Device_Control_Integration.md
│   ├── Phase_4_Automation_Smart_Rules_and_Scene_Management.md
│   ├── Phase_5_Testing_Error_Handling_Data_Management_and_Refinement.md
│   ├── Phase_6_Final_Integration_Documentation_and_Project_Completion.md
│   └── DEPLOYMENT_GUIDE.md
│
├── 📂 SOURCE_CODE/
│   └── src/                             // Complete Java source tree
│
├── 📄 TELEMETRY_LOGS/
│   └── ActivityLog.txt                  // Sample exported audit log
│
└── 📝 README.txt                        // Student details, system requirements, how to run
```

### Sample `README.txt` for Submission:
```text
PROJECT TITLE: Smart Home Automation Simulator
DEGREE: Bachelor of Computer Applications (BCA) / B.Tech / MCA
STUDENT NAME: [Your Name]
ROLL NUMBER: [Your Roll Number]
ACADEMIC YEAR: 2026

HOW TO RUN THE APPLICATION:
1. Make sure Java (JDK/JRE 17 or higher) is installed.
2. Double-click "run.bat" or double-click "SmartHomeSimulator.jar".
3. To view automated unit & integration test results, run "run_tests.bat".

PROJECT HIGHLIGHTS:
- Pure Software Simulation (No physical IoT hardware needed).
- Complete Java Swing MVC architecture.
- 5 Household Rooms, 13 Smart Appliances.
- Intelligent Rule Engine (Ambient Temp, Motion Pulse, Day/Night).
- 4 Macro Scenes (Good Morning, Movie Mode, Sleep Mode, Away Mode).
- 21-Point Automated SQA Test Suite with 100% Pass Rate.
```

---

## 8. Common Deployment Troubleshooting

| Issue / Error Message | Root Cause | Solution |
| :--- | :--- | :--- |
| **`'java' is not recognized as an internal or external command`** | Java is either not installed or the `bin` directory is missing from Windows `PATH`. | Install JDK 17/21 from Adoptium and ensure "Add to PATH" is checked during setup. Or edit System Environment Variables ➔ `Path` ➔ add `C:\Program Files\Java\jdk-17\bin`. |
| **Double-clicking `.jar` does nothing on Windows** | Windows has unassociated `.jar` files or assigned them to an archive tool (like WinRAR/7-Zip). | **Right-click** `SmartHomeSimulator.jar` ➔ **Open with** ➔ **Choose another app** ➔ Browse to `C:\Program Files\Java\jdk-17\bin\javaw.exe` ➔ Check *"Always use this app to open .jar files"*. Or simply double-click `run.bat`. |
| **`UnsupportedClassVersionError: ... has been compiled by a more recent version of Java`** | Application was compiled with a higher JDK (e.g. JDK 21) but target PC has an older JRE (e.g. Java 8). | Recompile the code on the target computer by double-clicking `build_and_run.bat`, or use `javac --release 17 ...`. |
| **`Could not find or load main class com.smarthome.SmartHomeApp`** | Missing `manifest.txt` or incorrect manifest entry when building the JAR. | Ensure `manifest.txt` contains `Main-Class: com.smarthome.SmartHomeApp` followed by a blank line, and re-run `build_and_run.bat`. |
