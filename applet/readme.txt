It is a simple multithreaded chat application with just a single room. It was written when I was just learning how socket programming in Java works.

It is assumed that jdk bin directory is in your classpath. If not then change the path in runservr.bat and runclint.bat.

1. Extract contents of applet.zip (which you have already done) to any folder.

2. In the folder where to have extracted the contents of the zip file, first run runservr.bat. Server will start. It listens to port 5000.
3. Execute runclint.bat to run the client applet.
	Or
4. Copy the client.html to root directory of your web server and the use a URL like http://localhost/client.html. If you just double click the client.html file then a SecurityException will be thrown and applet won't run.

Sukhwinder Singh
ssruprai@hotmail.com