# SOEN 342 Final Project - Personal Task Management System

Welcome to our final project repository! This is a command-line Personal Task Management System built entirely in Java. It doesn't use a GUI, but it handles the complete lifecycle of task management, collaborator assignments, and data persistency using object-oriented design principles. 

## What the System Does
* **Task Management:** Create, update, complete, and cancel tasks. You can break things down into subtasks, capped at a maximum of 20 subtasks per parent task.
* **Organization & Search:** Group tasks into projects and add custom tags. You can filter by due dates, tags, priority, and status. (Note: The system restricts you from having more than 50 open tasks that don't have a due date).
* **Activity Log:** Automatically tracks a timestamped history of everything you do (creating, updating, completing, or canceling tasks).
* **Recurring Tasks:** Easily set up daily, weekly, monthly, or custom recurrence patterns.
* **Collaborator Workloads:** Assign tasks to Senior, Intermediate, or Junior collaborators. The system strictly enforces workload limits (e.g., Seniors get a max of 2 open tasks). We also added a specific menu option to immediately flag and list any overloaded collaborators.
* **Data & Integrations:** * Full database persistency layer to save all your work.
  * Import/Export your whole database using CSV files.
  * **iCalendar Export:** We integrated the `iCal4j` library so you can export tasks with due dates into `.ics` format. If a task has subtasks, they get summarized in the description field so they don't clutter your calendar app.

## Architecture & Design
* **Gateway Pattern:** We used this pattern to safely connect our core domain layer to the external `iCal4j` library.
* **OCL Constraints:** All the strict business rules (like the max 20 subtasks, the 50 open task limit, and the positive integer limits for collaborators) are formally documented using Object Constraint Language.

## Required Deliverables
You'll find all our final artifacts in our project folder "FINAL ARTIFACTS" or on our wiki page "FINAL ARTIFACTS" at https://github.com/Steven01231/SOEN342-Winter2026/wiki/FINAL-ARTIFACTS :
* Use-Case Diagrams (Critical & Non-critical)
* UML Domain Model & Class Diagrams (including the updated Gateway pattern design)
* System Sequence Diagrams (SSDs) & Fully-Dressed Scenarios
* Operation Contracts
* The specific Sequence Diagram showing the iCal export functionality
* OCL Constraints Document

## How to Run It
1. Clone this repository to your local machine.
2. Make sure you have Java installed and that the `iCal4j` library is properly included in your build path.
3. Compile the code and initialize the database.
4. Run the main application file directly from your terminal.

## Demo Link

https://github.com/user-attachments/assets/3b526b90-d894-40c0-b117-6944f6ced7ad


---
**Team Members:**
| Name | ID | GitHub Username |
| :------- | :------- | :------- |
| Steven Dy | 40283742 | Steven01231 |
| Tiffany Andriamiharimanana |40283866  |  tiffsoa|
| Mridul Mridul | 40279215 | mrid105 |
