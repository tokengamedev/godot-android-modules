# GAML - Frequently asked questions


###### 1. Why minimum supported Android SDK version is 23 whereas Godot Engine 3.5 has minimum SDK 19?

Two reasons, first percentage of users as per google is more than 96% on devices greater than SDK 23 and second devices with lower versions will be older devices and probably not game ready.


###### 2. Why to rewrite the modules when already there are available modules from various users like DrMoriarty?

Although I have used the modules from various users and some I am not going to replace, there are few reasons to create the modules from scratch.

- Missing core functions: Some of the existing libraries have missing interfaces for core modules.

- Missing customizations: Some customizations are needed, which is available in the apis but not exposed through plugins. To get that done code for plugins has to be changed. 
  
- Maintenance and Upgrade issues: New features and upgrades are slow to be adopted by various modules, although users provide pull requests, but still slow.

- Distributed: Even if the developer wants to make a change to existing plugins, that means many projects to be maintained for a single game by the developer.   

Note: I understand the code maintainers for various plugins are busy in various projects, and it will be slow to change, but I appreciate their hard work which helped in many projects.

###### 3. How to add a new Plugin to the overall build?

It is simple, copy one of the existing modules and rename, modify and refactor as per your needs and then add your new plugin name into the **settings.gradle.kts**. Done.

