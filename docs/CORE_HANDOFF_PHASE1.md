# AI_Core API Documentation (Phase 1)

## 1. TimerStateMachine
The core engine for time management. It operates on a single-threaded scheduled executor that fires every 100ms.

**Features:**
* Thread-safe state transitions (`synchronized`).
* Supports `FORWARD`, `BACKWARD`, and `POMODORO` modes.
* Triggers UI updates via the `ITimerCallback` interface.

## 2. MilestoneScheduler
Calculates the dynamic daily workload based on the user's past efficiency and remaining timeline.

### Algorithm
The daily target computation uses the following formula to adjust for user efficiency:

$$T_{daily} = \lceil \frac{W_{total} - W_{current}}{D_{rem}} \times (2 - E_{history}) \rceil$$

**Variables:**
* $T_{daily}$: Target workload per day
* $W_{total}$: Total workload required
* $W_{current}$: Currently completed workload
* $D_{rem}$: Days remaining until deadline
* $E_{history}$: Historical efficiency ratio (e.g., 1.0 means 100% on track)

By using this logic, if a user has a historical efficiency of 0.8 (completes tasks slower than expected), the required daily target will mathematically increase to prevent them from falling behind.

## 3. DTO Definitions
We utilize Java 21 `record` constructs for memory-efficient and immutable payload transfers between the AI_Core and the external layers (Backend/Frontend). Example: `MilestoneDTO`.