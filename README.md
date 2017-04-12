# Bamboo-CloudShell-Sandbox-Plugin

## Prerequisite

1) CloudShell 8.0 and above, 'CloudShell Sandbox API' component must be installed.

2) Bamboo server 5.13 and above.

## Architecture

1) open port between Bamboo and the CloudShell Sandbox API Server (82 by default but configurable)



## Installation
1) Download the plugin jar from the releases tab

2) Navigate to the add-ons section in Bamboo administration page

3) Upload the jar file into the "Upload add-on" section



## Configuring CloudShell Snadbox in Bamboo
1) Open Bamboo administration page

2) Open "CloudShell Sandbox Settings"

3) Fill up all required fields.

![Alt text](pics/administration.png?raw=true)

### Adding a CloudShell task

Use "CloudShell Sandbox Builder" task to start a CloudShell Sandbox during Bamboo job execution. The Sandbox will start in CloudShell and the job next task will be executed only after the sandbox will complete the "Setup" phase.
The Sandbox details will be available using environment variables in other tasks in the build.

CloudShell Task builder
![Alt text](pics/cloudshell-task.png?raw=true)

CloudShell Task builder parameters
![Alt text](pics/cloudshell-taskconfiguration.png?raw=false)

Cloudshell Sandbox information via environment variables
![Alt text](pics/cloudshell-environmentvariables.png?raw=true)

#### Note 1: Sandboxes created during the job execution will be stopped after the job will end. Teardown proccess will be executed and tehe job will wait till completed before ending.
 
#### Note 2: CloudShell Bamboo plugin supprts only one Sandbox per job. 

Enjoy

Tomer
