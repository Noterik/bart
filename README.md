Bart
====

Bart is a interface for a the world outside the cluster to talk to our simple filesystem (fsxml), This version is very limited
at the moment and should be considered a placeholder until we finish the barney/smithers interaction to allow for a secure
way to read and possibly write into the filesystem (database). At the moment no https is used but that will be added soon.

1) Check out Bart in Eclipse
2) Build a war using the 'deploy-war' task with the provided build.xml
3) Deploy the war on a Tomcat server

You can now use http to talk to the system like (example)

http://[yourhost]:[yourport]/edna/domain/sneakers/user/daniel/profile **

** This rest uri should exists in your cloud and you should have the correct rights.