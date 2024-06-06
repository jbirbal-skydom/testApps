<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a name="readme-top"></a>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->



<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]



<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/github_username/repo_name">
    <img src="images/logo.png" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">project_title</h3>

  <p align="center">
    project_description
    <br />
    <a href="https://github.com/github_username/repo_name"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/github_username/repo_name">View Demo</a>
    ·
    <a href="https://github.com/github_username/repo_name/issues">Report Bug</a>
    ·
    <a href="https://github.com/github_username/repo_name/issues">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## Boilerplate App Development for iOS and Android

[![Product Name Screen Shot][product-screenshot]](https://example.com)

A streamlined setup for developing iOS and Android boilerplate apps.


This repository provides a detailed guide to set up development environments for creating boilerplate iOS and Android applications using Docker. For iOS, it includes the setup of a MacOS instance using Docker-OSX with installations of Xcode and Rust. For Android, it outlines setting up a Debian instance via QEMU-Docker, along with installations of Android Studio and Rust.

 `github_username`, `repo_name`, `twitter_handle`, `linkedin_username`, `email_client`, `email`, `project_title`, `project_description`

[back to top](#readme-top)



### Built With

* [![Next][Next.js]][Next-url]
* [![React][React.js]][React-url]
* [![Vue][Vue.js]][Vue-url]
* [![Angular][Angular.io]][Angular-url]
* [![Svelte][Svelte.dev]][Svelte-url]
* [![Laravel][Laravel.com]][Laravel-url]
* [![Bootstrap][Bootstrap.com]][Bootstrap-url]
* [![JQuery][JQuery.com]][JQuery-url]

[back to top](#readme-top)

<!-- GETTING STARTED -->
## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites

This is an example of how to list things you need to use the software and how to install them.

* WSL
  
* Docker
  
  ```sh
  curl -fsSL https://get.docker.com -o get-docker.sh
  sh get-docker.sh
  ```

* Mount drive

  ```sh
  sudo mount -t drvfs F: /mnt/f
  ```

### Installation

#### iOS Development

1. Clone the repo

   ```sh
   git clone https://github.com/sickcodes/Docker-OSX.git
   ```

2. Navigate into the directory and start the Docker container:
   1. 10.x: Good response but old now need XCode 12
      1. Rust Do not work
   2. 13.x : Bad graphics very buggy and crashed on GUI but still has the processing power.
      [X] Rust Works
      []  Electron do not ( some applications will not show `Safari`) 
   3. 14.x : Unknown
      1. Teamviewer or Anydesk crashes the system

   ```sh
    docker run -it     --device /dev/kvm     -p 50922:10022     -v "${PWD}/running/mac_hdd_ng(14.0_FullDev).img:/image"     -v /tmp/.X11-unix:/tmp/.X11-unix     -e "DISPLAY=${DISPLAY:-:0.0}"     -v "${PWD}/run_scripts/Launch.sh:/home/arch/OSX-KVM/Launch.sh"     -v "${PWD}/run_scripts/LaunchReal.sh:/home/arch/LaunchReal.sh"     -e RAM=28     -e AUDIO_DRIVER="id=none,driver=none"   -e EXTRA='-smp 16,sockets=1,cores=8,threads=2'  -e CPU='Haswell-noTSX'   -e CPUID_FLAGS='kvm=on,vendor=GenuineIntel,+invtsc,vmware-cpuid-freq=on'    sickcodes/docker-osx:naked
   ```

3. Copy / download Xcode 12 for the latest compatible version for Catalina. *Apple changed the MacOS graphics engine in Big Sur*

4. SSHFS needs to be active
   1. Look in the settings and turn it on

5. Install Xcode and Rust inside the MacOS container:
   1. Xcode
      1. unzip

           ```sh
           xip -x XCode.xip
          sudo xcode-select -s /Applications/Xcode.app
           ```

      2. install the CMD tools (This will provide rust a compiler)

           ```sh
            xcode-select --install
            sudo xcodebuild -license
            xcode-select -p
           ```

      3. Copy to application folder and open

   2. Android studio
      1. Download
      2. Install NDK
      3. Simulator do not work becasue the CPU passthorugh will not allow

   3. Rust
      1. Download

            ```sh
            curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh
            ```

#### Android Development using QEMU

1. Clone the QEMU-Docker repository:

   ```sh
   git clone https://github.com/qemus/qemu-docker.git
   ```

2. Set up the Debian Docker environment: (using WSL)

   1. use the provided `compose.yml` to build the environment. The environment is meant to be lightweight and effective with a desktop front end.

    ```sh
    docker compose up
    ```

3. Install Android Studio and Rust:

    ```sh
    sudo apt install android-studio
    curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh
    ```

#### Android Development using WSL (BEST)

1. Download Adroid studio
   1. uncompress
   2. ad to .bashrc `alias android-studio="$HOME/Applications/android-studio/bin/studio.sh"`

2. Edit the /etc/profile
   1. sudo chown <USERNAME> /etc/profile
   2. `code /etc/profile`
   3. add `export ANDROID_SDK_ROOT=/home/skydom/Android/Sdk` and `PATH=$PATH:$ANDROID_SDK_ROOT/platform-tools`

3. Emulation
   1. Graphical and Hardware Acceleration: Android emulators often require direct access to hardware acceleration features (like Intel HAXM or AMD-V) and graphical output, which WSL2 does not fully support.
   2. install Java
      1. Selecting the Correct JBR Version:
         1. JBR (vanilla): This is the standard version of JBR without extra features like JCEF. It's typically sufficient for most development needs unless you specifically need browser integration within Java applications.
         2. JBR with JCEF (fastdebug): Includes JCEF and is built with additional debugging support. This version is useful if you need to embed a web browser in your Java applications and require extensive debugging capabilities.
         3. JBRSDK: This likely includes development tools and SDK components, making it a good choice if you are looking for a comprehensive development setup.
         4. JBRSDK (fastdebug): Similar to the JBRSDK but with enhanced debugging capabilities. Not necessary unless you are troubleshooting complex issues in the JDK itself.
         5. JBRSDK with JCEF: Includes both the development tools and the Chromium Embedded Framework. It's more than you need for standard Android SDK management tasks.
      2. Given your needs (command line usage for Android SDK management), the JBR (vanilla) or JBRSDK would be suitable.
      3. Extract Java at  `C:\Program Files\Java\jbr-17.0.11`
      4. Enter `JAVA_HOME` as the `variable name` and the path to your Java JDK directory as the variable value (e.g., C:\Program Files\Java\jbr-17.0.11).
      5. Update `PATH`:
         1. `Path` variable and add `%JAVA_HOME%\bin`.
      6. `java -version`
   3. install SDK [Platform-Tools]
      1. Extract the Downloaded File:
         1. After downloading, extract the zip file to a directory of your choice, preferably one without space characters in the path (e.g., C:\Android\platform-tools).
      2. Set Up Environment Variables:
         1. Select the Path variable and add the path to the platform-tools folder
      3. Verify the Installation `adb version`
   4. Add SDK Variable
         1. Enter ANDROID_HOME as the variable name and the path to your Android SDK directory as the variable value (e.g., C:\Users\<Your-Username>\AppData\Local\Android\Sdk).
         2. Add the following Path  entries:\
            1. `%ANDROID_HOME%\cmdline-tools\latest\bin`
            2. `%ANDROID_HOME%\platform-tools`
   5. Install `sdkmanager` and `avdmanager` that is included in the [Command Line Tools] 
      1. unzp to `C:\Android\cmdline-tools`
      2. add to path `C:\Android\cmdline-tools\bin>`
   6. SDK:
      1. `sdkmanager "platforms;android-29"`
      2. Variable name: `ANDROID_SDK_ROOT` Variable value: `C:\Android`
   7. Create emulation:
      1. `sdkmanager "system-images;android-29;default;x86"`
      2. `avdmanager create avd -n myAVD -k "system-images;android-29;default;x86"`
      3. `emulator -avd myAVD`
      4. `adb devices`
   8. edit settings for emulation:
      1. `C:\Users\<YourUsername>\.android\avd\<AVDName>.avd\`
      2. Fix boot:

         ```js
         fastboot.forceChosenSnapshotBoot = no
         fastboot.forceColdBoot = yes
         fastboot.forceFastBoot = no
         ```

      3. Fix Keys:

         ```js
         hw.keyboard = yes
         hw.mainKeys = no
         hw.dPad = no
         ```

      4. Save

[back to top](#readme-top)

## Notes

### Port issues

`(HTTP code 500) server error - Ports are not available: exposing port TCP 0.0.0.0:50922 -> 0.0.0.0:0: listen tcp 0.0.0.0:50922: bind: An attempt was made to access a socket in a way forbidden by its access permissions.`

```sh
net stop winnat
net start winnat
```
<!-- USAGE EXAMPLES -->
## Usage

Use this space to show useful examples of how a project can be used. Additional screenshots, code examples and demos work well in this space. You may also link to more resources.

_For more examples, please refer to the [Documentation](https://example.com)_

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ROADMAP -->
## Roadmap

* [ ] Feature 1
* [ ] Feature 2
* [ ] Feature 3
  * [ ] Nested Feature

See the [open issues](https://github.com/github_username/repo_name/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTACT -->
## Contact

Your Name - [@twitter_handle](https://twitter.com/twitter_handle) - email@email_client.com

Project Link: [https://github.com/github_username/repo_name](https://github.com/github_username/repo_name)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

* []()
* []()
* []()

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/github_username/repo_name.svg?style=for-the-badge
[contributors-url]: https://github.com/github_username/repo_name/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/github_username/repo_name.svg?style=for-the-badge
[forks-url]: https://github.com/github_username/repo_name/network/members
[stars-shield]: https://img.shields.io/github/stars/github_username/repo_name.svg?style=for-the-badge
[stars-url]: https://github.com/github_username/repo_name/stargazers
[issues-shield]: https://img.shields.io/github/issues/github_username/repo_name.svg?style=for-the-badge
[issues-url]: https://github.com/github_username/repo_name/issues
[license-shield]: https://img.shields.io/github/license/github_username/repo_name.svg?style=for-the-badge
[license-url]: https://github.com/github_username/repo_name/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/linkedin_username
[product-screenshot]: images/screenshot.png
[Next.js]: https://img.shields.io/badge/next.js-000000?style=for-the-badge&logo=nextdotjs&logoColor=white
[Next-url]: https://nextjs.org/
[React.js]: https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB
[React-url]: https://reactjs.org/
[Vue.js]: https://img.shields.io/badge/Vue.js-35495E?style=for-the-badge&logo=vuedotjs&logoColor=4FC08D
[Vue-url]: https://vuejs.org/
[Angular.io]: https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white
[Angular-url]: https://angular.io/
[Svelte.dev]: https://img.shields.io/badge/Svelte-4A4A55?style=for-the-badge&logo=svelte&logoColor=FF3E00
[Svelte-url]: https://svelte.dev/
[Laravel.com]: https://img.shields.io/badge/Laravel-FF2D20?style=for-the-badge&logo=laravel&logoColor=white
[Laravel-url]: https://laravel.com
[Bootstrap.com]: https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white
[Bootstrap-url]: https://getbootstrap.com
[JQuery.com]: https://img.shields.io/badge/jQuery-0769AD?style=for-the-badge&logo=jquery&logoColor=white
[JQuery-url]: https://jquery.com

<!-- links -->

[Platform-Tools]: https://developer.android.com/tools/releases/platform-tools
[Command Line Tools]: https://developer.android.com/studio#cmdline-tools
[JDK]: https://www.oracle.com/java/technologies/downloads/#java11