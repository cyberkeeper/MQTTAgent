# MQTT Agent

## Introduction

This is a tool to help manage MQTT projects. The application is written in Java with a Swing Graphical User Interface (GUI). The tool has functionality to connect to, publish and subscribe to any specified topic. 
The original aim of tool was demonostrate an implementation of publisher/subscriber pattern for edcuational purposes. Extra functionality was added to demonstrate other Java concepts. While not 
necesary for functionality, it is easier to demonstrate some things in more realistic scenarios. They also make the application more 'real'.

- sound, positive and negative sounds added for successful and unsuccessful connection to an MQ broker
- icons on JLabel change to indicate connection/disconnection
- threads, the sounds are played in separate threads to avoid interruption to the user
- interface, used to get events from the MqttCallback class
- internationlisation, English and Spanish resource files are included in a resource bundle.
- Maven build, a pom.xml file is included for building via Maven

## Screenshots

Connection screen.

![image](https://github.com/cyberkeeper/MQTTAgent/assets/40637121/092b454c-6a73-49b1-bb93-38fd66623329)

If no authentication has been setup on the broker then the user id and password can be left blank. To idenfiy the client for publishing and subscribing a unique identifier
is required, type in manually or press Generate. The Generate button will create a 36 character id for the current session.

Publish screen.

![image](https://github.com/cyberkeeper/MQTTAgent/assets/40637121/a354304f-8fa8-4f8d-ba28-fdd31c740bba)

Any topics typed in the Topic drop down list box will be remembered for the duration of this session. 

Subscribe screen.

![image](https://github.com/cyberkeeper/MQTTAgent/assets/40637121/c6f2fc30-fb6a-4d6b-aa8d-e108089f6d79)

Type in any topics that you want to subscribe to. All subscribed to topics will be stored in the drop down list. To unsubscribe to a topic select it from the drop down list and click
the Unsubscribe button. 

## Design

The code matched the design documents supplied in the tutorials. The code has been been design to be easy to maintain and expand.

## Environment

The code was written in JetBrains IntelliJ Community edition v2023.2. The SDK was Oracle Open JDK20, level 20 "No new language features". The operating system used was Microsoft Windows 10 and 11. 
The Jar file was tested by running in a non-adminisrator Powershell mode.
A pom.xml file is included for building using Maven. This has been tested using 'Apache-Maven-3.9.7'

## Usage

Tme MQTT Agent tool can be run from the command line or double clicked if your OS is set enable that.
