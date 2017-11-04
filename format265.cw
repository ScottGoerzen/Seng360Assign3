# Seng360Assign3
Assignment 3 For Seng 360 Fall 2017

by Jens Weber - Monday, 30 October 2017, 3:50 PM
 
Hello Phil (and all),

you do not have to use HTTP or any specific networking protocol. However, to make this assignment more realistic, your client and server should run in different processes. One relatively simple way to do so is to use Java RMI. Here is a tutorial:

http://www.ejbtutorial.com/java-rmi/java-rmi-example-simple-chat-program-between-server-and-client



Cheers

Jens

by Jens Weber - Tuesday, 31 October 2017, 11:29 AM
 
Hello Alex,

Question 1:



Yes, several properties should be selectable at the same time.



Question 2:



Integrity just ensures that the message has not been changed while in transit between client and server. You can ensure this by using message authentication codes (with AES) (see your question 5).



Authentication also requires that users authenticate. This can be done using a password authentication on client and server, respectively.



Question 3: You should use symmetric encryption for confidentiality, as it is much faster than asymmetric crypto. I recommend AES - but you can use a different cipher. I suggest to create a new symmetric session key for each new session. This can be done on the client. The key can be sent to the server using asymmetric crypto (encrypted with the server's public key).

Using a pre-shared symmetric secret is difficult in practice (if you consider the general case of having many different users and clients). How do you safely pre-share that key? What do you do if the key is leaked? You would have to install new keys to all installed clients (thousands, millions...)



Question 5: Yes, AES works only with symmetrical keys. Use DES or another asymmetrical cipher for establishing the symmetrical session key and ensuring integrity in that process.



Question 6: It is sufficient for this assignment to store the public/private keys in a protected directory. Here is some sample code: http://esus.com/programmatically-generating-public-private-key/

Alternatively, and if you are interested in doing it the "recommended way", you can read up on Java's KeyStore database, which can store and retrieve keys: https://docs.oracle.com/javase/8/docs/api/java/security/KeyStore.html
