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

  <h3>5. Optional Tools</h3>
  <ul>
    <li>Git – Version control system: <a href="https://git-scm.com/downloads">Download Git</a></li>
    <li>Homebrew (macOS) – Package manager: <a href="https://brew.sh/">Install Homebrew</a></li>
    <li>Android Studio (if building/running Android version): <a href="https://developer.android.com/studio">Download Android Studio</a></li>
  </ul>
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
