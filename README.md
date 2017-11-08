# Seng360Assign3
Assignment 3 For Seng 360 Fall 2017

This Program uses 3 terminal windows.

First open all windows to the tutorial/Client/src folder and run in the first

	rmiregistry

On one terminal window compile all files with the command 

	javac test/RMIInterface.java test/RMICInterface.java test/ClientOperation.java test/ServerOperation.java

On the same window run 

	java test/ServerOperation
and enter the password 'GROOT' before selecting the desired security options.

On the last window run 

	java test/ClientOperation 

and enter a arbitrary Username and the password 'Rocket' to authenticate the user before selecting the desired security options.

*Note that the server must be run first, and the client to follow before typing somthing into the server field to indicate readiness*

Next type a affirmative response into the Server console to indicate the Client is ready. -> this action serves to hold the server operation until the client has been established before trying to connect. 
If this order is not followed, an error connecting to the client will arise.

You can now type freely as the Client or Server to send messages to the other party.

Security options can be changed by typing a '-' followed by a command into the input field.

Commands include:

	-ct	: Confidentiality true : Turns on encryption of outgoing messages

	-cf	: Confidentiality false : Turns off encryption of outgoing messages

	-it 	: Integrity Checks enabled : Turns on checking of message integrity via message authentication codes (MAC)

	-if 	: Integrity Checks disabled : Turns off checking of message integrity via message authentication codes (MAC)

Both security options are enabled by default, but are selectable and set by the initiation sequence in the menu feature.

Authorization is checked at login on both Server and Client side by means of comparing the hash of the input password to the hash saved in an access controlled directory.

When the client and the server connect to each other, they send a authenication message via public/private keys in asymmetric cryptograpy and when the client contacts the server, a session key established for symmetric cryptography with AES is returned.

All later encryption is done by AES encryption with the session key established at the first step.

Hashing is done via the MD5 hashing scheme.

Integrity is verified by means of passing a message authentication code (MAC)  along with the message. The MAC consists of the message being hashed with MD5, encrypted with AES and then converted back to a string. 
