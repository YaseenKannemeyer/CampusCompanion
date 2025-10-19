<section id="prerequisites">
  <h2>Prerequisites</h2>
  <p>Before installing and running the <strong>StudentTimeTable</strong> application, ensure the following are installed on your system:</p>

  <h3>1. Java</h3>
  <p><strong>Version:</strong> JDK 24 (Java SE 24)</p>

  <p><strong>Check Installation:</strong></p>
  <pre><code>java -version
javac -version</code></pre>

  <p><strong>Install Java:</strong></p>
  <ul>
    <li><strong>Oracle JDK 24:</strong> <a href="https://www.oracle.com/java/technologies/javase/jdk24-archive-downloads.html">Download here</a></li>
    <li><strong>OpenJDK 24:</strong> <a href="https://jdk.java.net/24/">Download here</a></li>
    <li><strong>Linux (Debian/Ubuntu) via terminal:</strong>
      <pre><code>sudo apt update
sudo apt install openjdk-24-jdk</code></pre>
    </li>
    <li><strong>macOS (Homebrew) via terminal:</strong>
      <pre><code>brew install openjdk@24</code></pre>
    </li>
  </ul>

  <h3>2. Apache Derby (Embedded Database)</h3>
  <p>Used as the embedded database for offline storage.</p>

  <p><strong>Check installation via terminal (if Derby scripts are included):</strong></p>
  <pre><code>java -jar derbyrun.jar sysinfo</code></pre>

  <p><strong>Install Derby:</strong></p>
  <ul>
    <li><strong>Linux/macOS via terminal:</strong>
      <pre><code>wget https://downloads.apache.org/db/derby/db-derby-10.14.2.0/db-derby-10.14.2.0-bin.zip
unzip db-derby-10.14.2.0-bin.zip</code></pre>
    </li>
    <li><strong>Windows:</strong> <a href="https://db.apache.org/derby/derby_downloads.html">Download Apache Derby</a></li>
    <li><strong>Maven Dependency (embedded in project):</strong>
      <pre><code>&lt;dependency&gt;
  &lt;groupId&gt;org.apache.derby&lt;/groupId&gt;
  &lt;artifactId&gt;derby&lt;/artifactId&gt;
  &lt;version&gt;10.14.2.0&lt;/version&gt;
&lt;/dependency&gt;</code></pre>
    </li>
  </ul>

  <h3>3. Python</h3>
  <p><strong>Version:</strong> Python 3.12 or higher</p>

  <p><strong>Check Installation:</strong></p>
  <pre><code>python3 --version
# or
python --version</code></pre>

  <p><strong>Install Python:</strong></p>
  <ul>
    <li>Download from <a href="https://www.python.org/downloads/">Python Downloads</a></li>
    <li>Ensure you select “<strong>Add Python to PATH</strong>” during installation</li>
  </ul>

  <h3>4. Python Packages</h3>
  <p>Install required Python dependencies using <code>pip</code>:</p>
  <pre><code>pip install chatterbot==1.0.5
pip install flask==2.3.6
pip install flask_cors==3.2.2</code></pre>

  <p>These packages power the chatbot server embedded in the application.</p>

 
</section>


<section id="installation">
  <h2>Installation & Packaging</h2>
  <p>This section explains how to clone, build, and package the <strong>StudentTimeTable</strong> Java application into a standalone desktop app with an icon using <code>jpackage</code>. This allows users to run the app without manually installing Java or Python.</p>

  <h3>0. Clone the Project</h3>
  <p>Clone the repository from GitHub:</p>
  <pre><code>git clone https://github.com/YaseenKannemeyer/CampusCompanion.git
cd CampusCompanion</code></pre>

  <h3>1. Prerequisites</h3>
  <ul>
    <li>Ensure Java JDK 24 is installed and <code>java</code>/<code>javac</code> are available in PATH.</li>
    <li>Ensure Apache Derby and Python dependencies are available (embedded in the app).</li>
    <li>Have your application JAR ready (e.g., <code>StudentTimeTable-1.0.1-shaded.jar</code>).</li>
    <li>Prepare an icon file:
      <ul>
        <li>Windows: <code>icon.ico</code></li>
        <li>macOS/Linux: <code>icon.png</code></li>
      </ul>
    </li>
  </ul>

  <h3>2. Package for Windows</h3>
  <p>Use <code>jpackage</code> to create a Windows installer or EXE:</p>
  <pre><code>jpackage \
  --type exe \
  --name "CampusCompanion" \
  --app-version 1.0.1 \
  --input target \
  --main-jar StudentTimeTable-1.0.1-SNAPSHOT-shaded.jar \
  --main-class mycput.ac.za.studenttimetable.AppLauncher \
  --icon src/main/resources/icons/StudentIcon.ico \
  --java-options "--enable-preview" \
  --vendor "CPUT" \
  --copyright "CPUT"
</code></pre>
  <p>This will generate an EXE installer that includes the embedded JRE and all dependencies.</p>

  <h3>3. Package for macOS</h3>
  <p>Create a DMG or app bundle for macOS:</p>
  <pre><code>jpackage \
  --type dmg \
  --name "StudentTimeTable" \
  --app-version 1.0.1 \
  --input target \
  --main-jar StudentTimeTable-1.0.1-SNAPSHOT-shaded.jar \
  --main-class mycput.ac.za.studenttimetable.AppLauncher \
  --icon src/main/resources/icons/StudentIcon.icns \
  --java-options "--enable-preview" \
  --vendor "CPUT" \
  --copyright "CPUT"</code></pre>
  <p>The resulting DMG or .app bundle can be distributed to macOS users without requiring a separate Java installation.</p>

  <h3>4. Package for Linux</h3>
  <p>Create a Linux installer (e.g., DEB or RPM) or AppImage:</p>
  <pre><code>jpackage \
  --type deb \
  --name "CampusCompanion" \
  --app-version 1.0.1 \
  --input target \
  --main-jar StudentTimeTable-1.0.1-SNAPSHOT-shaded.jar \
  --main-class mycput.ac.za.studenttimetable.AppLauncher \
  --icon src/main/resources/icons/StudentIcon.png \
  --java-options "--enable-preview" \
  --vendor "CPUT" \
  --copyright "CPUT"
</code></pre>
  <p>Replace <code>--type deb</code> with <code>rpm</code> for RPM-based distributions (Fedora, CentOS) or <code>app-image</code> for a universal AppImage.</p>

  <h3>5. Notes & Tips</h3>
  <ul>
    <li>The <code>--input</code> folder should contain your JAR and any resources required by the app (icons, Derby DB folder, Python embedded files).</li>
    <li>Ensure that your Python ChatterBot server is embedded in the JAR or alongside the packaged app.</li>
    <li>Test each installer on a clean OS environment to verify that the app runs without additional installations.</li>
    <li>Use <code>--java-options "--enable-preview"</code> if using preview features in Java 24.</li>
    <li>For Windows, the EXE installer can be double-clicked to install the app and add a desktop/start menu icon.</li>
    <li>For macOS, the DMG can be dragged to Applications, creating a clickable app icon.</li>
    <li>For Linux, DEB or RPM packages can be installed via package manager, creating a desktop entry and icon.</li>
  </ul>
</section>

---
## 🗂 Project Structure
```graphql
├── db
│   └── Project2FinalDB
│       ├── log
│       ├── seg0
│       └── tmp
├── src
│   └── main
│       ├── java
│       │   ├── fonts
│       │   │   └── Poppins
│       │   └── mycput
│       │       └── ac
│       │           └── za
│       │               ├── openaiclient
│       │               └── studenttimetable
│       │                   ├── connection
│       │                   ├── dao
│       │                   ├── domain
│       │                   └── resources
│       │                       ├── icons
│       │                       └── Poppins
│       └── resources
│           ├── icons
│           ├── Poppins
│           └── Vid
└── target
    ├── classes
    │   ├── icons
    │   ├── mycput
    │   │   └── ac
    │   │       └── za
    │   │           ├── openaiclient
    │   │           └── studenttimetable
    │   │               ├── connection
    │   │               ├── dao
    │   │               └── domain
    │   ├── Poppins
    │   └── Vid
    ├── generated-sources
    │   └── annotations
    ├── maven-archiver
    └── maven-status
        └── maven-compiler-plugin
            └── compile
                └── default-compile

```

---
<section id="maintenance">
  <h2>Maintenance & Updates</h2>
  <p>This section outlines how to maintain, update, and release new versions of the <strong>StudentTimeTable</strong> desktop application. Regular maintenance ensures stability, compatibility, and security for all platforms.</p>

  <h3>1. Pull Latest Changes</h3>
  <p>Before making updates, pull the latest version of the project from GitHub:</p>
  <pre><code>git pull origin main</code></pre>
  <p>This ensures you are working on the most recent codebase.</p>

  <h3>2. Update Maven Dependencies</h3>
  <p>Use Maven to check for and update dependency versions defined in the <code>pom.xml</code> file:</p>
  <pre><code>mvn versions:display-dependency-updates</code></pre>
  <p>To automatically update to the latest stable versions:</p>
  <pre><code>mvn versions:use-latest-releases</code></pre>
  <p>After updating, verify that the application still compiles and runs correctly.</p>

  <h3>3. Update Python Dependencies</h3>
  <p>If you have updated Python packages (e.g., Flask, ChatterBot), update and freeze dependencies:</p>
  <pre><code>pip install --upgrade flask chatterbot flask_cors
pip freeze > requirements.txt</code></pre>
  <p>This ensures future installations use the correct versions.</p>

  <h3>4. Database Maintenance (Apache Derby)</h3>
  <ul>
    <li>Backup your Derby database periodically:
      <pre><code>cp -r db/Project2FinalDB db/backup/Project2FinalDB_$(date +%Y%m%d)</code></pre>
    </li>
    <li>Use Derby’s <code>ij</code> tool or SQL scripts to perform schema updates or cleanups as needed.</li>
    <li>Ensure database versioning is managed during each new release.</li>
  </ul>

  <h3>5. Rebuild the Application</h3>
  <p>After updating dependencies or making code changes, rebuild the shaded JAR:</p>
  <pre><code>mvn clean package</code></pre>
  <p>This produces an updated JAR file, for example:</p>
  <pre><code>target/StudentTimeTable-1.0.2-shaded.jar</code></pre>

  <h3>6. Repackage with <code>jpackage</code></h3>
  <p>Once rebuilt, repackage the new version for all supported platforms (Windows, macOS, Linux) using <code>jpackage</code>:</p>
  <pre><code>jpackage \
  --input target/ \
  --name StudentTimeTable \
  --main-jar StudentTimeTable-1.0.2-shaded.jar \
  --main-class mycput.ac.za.studenttimetable.AppLauncher \
  --icon resources/icons/icon.png \
  --app-version 1.0.2 \
  --type dmg</code></pre>
  <p>Replace <code>--type</code> with <code>exe</code>, <code>dmg</code>, or <code>deb</code> depending on your target OS.</p>

  <h3>7. Tag and Push a New Release</h3>
  <p>Once you’ve tested the new build, create and push a new version tag on GitHub:</p>
  <pre><code>git add .
git commit -m "Release v1.0.2 - UI updates and dependency improvements"
git tag v1.0.2
git push origin main --tags</code></pre>
  <p>This will mark the new version and prepare it for release packaging.</p>

  <h3>8. Publish Release on GitHub</h3>
  <p>Finally, upload the packaged app (EXE, DMG, or DEB) to your GitHub Releases page:</p>
  <ol>
    <li>Go to your repository’s <a href="https://github.com/YaseenKannemeyer/CampusCompanion/releases">Releases</a> section.</li>
    <li>Click <strong>“Draft a new release”</strong>.</li>
    <li>Select the new tag (e.g., <code>v1.0.2</code>).</li>
    <li>Upload your packaged installer(s).</li>
    <li>Add release notes describing changes, bug fixes, or improvements.</li>
    <li>Click <strong>“Publish release”</strong>.</li>
  </ol>

  <h3>9. General Maintenance Tips</h3>
  <ul>
    <li>Test major updates on all target platforms before publishing.</li>
    <li>Keep JDK, Maven, and Python versions in sync with your <code>README</code> prerequisites.</li>
    <li>Regularly clean your Maven build directory with <code>mvn clean</code> to avoid caching issues.</li>
    <li>Monitor security advisories for dependencies (e.g., Jackson, OkHttp, Flask).</li>
    <li>Backup your Derby database before every major release.</li>
  </ul>

  <p>By following these maintenance steps, the <strong>StudentTimeTable</strong> application remains stable, secure, and up-to-date across all supported operating systems.</p>
</section>

