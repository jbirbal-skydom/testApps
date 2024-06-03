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

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With

* [![Next][Next.js]][Next-url]
* [![React][React.js]][React-url]
* [![Vue][Vue.js]][Vue-url]
* [![Angular][Angular.io]][Angular-url]
* [![Svelte][Svelte.dev]][Svelte-url]
* [![Laravel][Laravel.com]][Laravel-url]
* [![Bootstrap][Bootstrap.com]][Bootstrap-url]
* [![JQuery][JQuery.com]][JQuery-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>



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
    docker run -it     --device /dev/kvm     -p 50922:10022     -v "${PWD}/running/mac_hdd_ng_13-2-1_XCode-.img:/image"     -v /tmp/.X11-unix:/tmp/.X11-unix     -e "DISPLAY=${DISPLAY:-:0.0}"     -v "${PWD}/run_scripts/Launch.sh:/home/arch/OSX-KVM/Launch.sh"     -v "${PWD}/run_scripts/LaunchReal.sh:/home/arch/LaunchReal.sh"     -e RAM=28     -e AUDIO_DRIVER="id=none,driver=none"   -e EXTRA='-smp 16,sockets=1,cores=8,threads=2'     sickcodes/docker-osx:naked
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

<p align="right">(<a href="#readme-top">back to top</a>)</p>


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

- [ ] Feature 1
- [ ] Feature 2
- [ ] Feature 3
    - [ ] Nested Feature

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