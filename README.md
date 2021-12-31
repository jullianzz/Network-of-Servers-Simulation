# Network of Servers Simulation w/ Performance Evaluation

A simulator that models the communication between an abstract network of computing resources (i.e. servers, processors, I/O, memory, etc.) for monitoring and performance evaluation purposes. All computing resources are modelled assuming Markovian arrival of requests and Markovian request processing times, which is to assume a Poisson arrival processes and exponentially distributed processing times. Performance metrics like utilization (UTIL), average queue length at the server (QLEN), average response time (TRESP) of each request, and average wait time (TWAIT) of each request are computed using monitor events. 

### Download and Run
* Download the version in the main branch
* Navigate to local directory containing the files and compile all .java files
* Run Simulator main using ```java Simulator <param0> ... <param16>```
1.  param0: length of simulation time in milliseconds 
2.  param1: average arrival rate of requests at the system λ
3.  param2: avaerage service time T_s0 at Server0 (S0)
4.  param3: average service time T_s1 at Server1 (S1)
5.  param4: average service time T_s2 at Server2 (S2)
6.  param5: service time t1 at Server3 (S3)
7.  param6: probability p1 of service time t1 at S3 
8.  param7: service time t2 at S3
9.  param8: probability p2 of service time t2 at S3
10. param9: service time t3 at S3
11. param10: probability p3 of service time t3 at S3
12. param11: K_2, maximum length of the queue expressed in number of requests at S2
13. param12: routing probability p0,1 that a request will go from S0 to S1
14. param13: routing probability p0,2 that a request will go from S0 to S2
15. param14: routing probability p3,out that a request will exit the system from S3
16. param15: routing probability p3,1 that a request will go from S3 back to S1
17. param16: routing probability p3,2 that a request will go from S3 back to S2


### Event-Modelling Branch (Step 1)
Developed Code to Generate Random Numbers that 
* Implemented the Java function with the following prototype ```double getExp(double lambda)``` in java class file ```Exp.java```. The function accepts one parameter lambda λ, which is the rate of arrival of requests. 
* Implemented a Java class called Event in java class file ```Event.java``` that represents a generic timed event with 


### Single Server Queue Branch (Step 2)

### Dual Server Queuing System Branch (Step 3)


### Multiple Server Simulation (Step 4)


### Relections and Future Work 

### Please Read
*** ALL code written in this repository is under the authorship of Julia Zeng (@jullianzz), who belongs to the Electrical & Computer Engineering Department at Boston University. All code is written strictly for educational purposes and not authorized for redistribution or re-purposing in any domain or by any individual or enterprise. ***