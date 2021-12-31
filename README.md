# Network of Servers Simulation w/ Performance Evaluation

A simulator that models the communication between an abstract network of computing resources (i.e. servers, processors, I/O, memory, etc.) for monitoring and performance evaluation purposes. All computing resources are modelled assuming Markovian arrival of requests and Markovian request processing times, which is to assume a Poisson arrival processes and exponentially distributed processing times. Performance metrics like utilization (UTIL), average queue length at the server (QLEN), average response time (TRESP) of each request, and average wait time (TWAIT) of each request are computed using monitor events. 

### Download and Run
* Download the version in the main branch
* Navigate to local directory containing the files and compile all .java files
* Run Simulator main using ```java Simulator `<param0>` p```

### Event-Modelling Branch (Step 1)
Developed Code to Generate Random Numbers that 
* Implemented the Java function with the following prototype ```double getExp(double lambda)``` in java class file ```Exp.java```. The function accepts one parameter lambda λ, which is the rate of arrival of requests. 
* Implemented a Java class called Event in java class file ```Event.java``` that represents a generic timed event with 


### Single Server Queue Branch (Step 2)

### Dual Server Queuing System Branch (Step 3)


### Multiple Server Simulation (Step 4)


### Relections and Future Work 