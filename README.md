<h1 align="center">JytChat</h1>
<p align="center">User Identifier System for Youtube Live Chat</p>

[한국어](README.kr.md)

## Introduction
This app is Youtube Live Chat Moderation tool. You may view and save the chat, send the chat. But there is one more thing...

This was originally created for school online classes. It provides user attendance checking system, so that teachers can check students' attendance easily. Moreover, it can be also used to remove the anonymity.

This is still in development! The program is written in Korean.

## Features
This app provides several helpful features for Youtube Live Chat at live streaming or premieres.
* View chat inside the app
* Send chat inside the app (requires login)
* Save chat log


* User attendance checking system
  * Viewers can prove their attendance by sending `!check myInfo` command to chat. (for example, `!check 12345 ABC`)
* Limiting chat permission (requires login and moderation permission)
  * Normal mode: everyone can send chat
  * Attendance mode: only users who checked their attendance can send chat
  * Moderator mode: only the owner and moderators can send chat
  * If user without permission sends the chat, the chat is deleted. You may manage the configuration to automatically ban the user, temporarily or permanently.

## Download
Check the [releases](https://github.com/HURDOO/jytchat/releases) tab!

## Compile
Compile with Java 11 & Gradle. You'll need platform-specific javafx libraries. (Included in build.gradle)

## Credits
* Used [YoutubeLiveChat](https://github.com/HURDOO/YouTubeLiveChat), a java library that fetches youtube live chat.
* Thanks to my middle school broadcasting club members, I made this for you.
  * Don't be stressed out by mischiefs anymore! Now, you may know who they are :)
