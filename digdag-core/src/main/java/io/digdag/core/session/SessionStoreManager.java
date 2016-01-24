package io.digdag.core.session;

import java.util.List;
import java.time.Instant;
import com.google.common.base.*;
import io.digdag.spi.RevisionInfo;
import io.digdag.client.config.Config;
import io.digdag.core.repository.ResourceConflictException;
import io.digdag.core.repository.ResourceNotFoundException;

public interface SessionStoreManager
{
    SessionStore getSessionStore(int siteId);

    Instant getStoreTime();

    // for WorkflowExecutor.enqueueTask
    StoredSessionAttemptWithSession getAttemptWithSessionById(long attemptId)
        throws ResourceNotFoundException;

    // for WorkflowExecutor.runUntilAny
    boolean isAnyNotDoneSessions();

    // for WorkflowExecutor.enqueueReadyTasks
    List<Long> findAllReadyTaskIds(int maxEntries);

    // for WorkflowExecutorManager.IncrementalStatusPropagator.propagateStatus
    List<TaskStateSummary> findRecentlyChangedTasks(Instant updatedSince, long lastId);

    // for WorkflowExecutorManager.propagateAllBlockedToReady
    List<TaskStateSummary> findTasksByState(TaskStateCode state, long lastId);

    boolean requestCancelAttempt(long attemptId);

    int trySetRetryWaitingToReady();

    interface TaskLockAction <T>
    {
        T call(TaskControlStore lockedTask);
    }

    interface TaskLockActionWithDetails <T>
    {
        T call(TaskControlStore lockedTask, StoredTask storedTask);
    }

    // overload for polling
    <T> Optional<T> lockTaskIfExists(long taskId, TaskLockAction<T> func);

    // overload for taskFinished
    <T> Optional<T> lockTaskIfExists(long taskId, TaskLockActionWithDetails<T> func);

    // overload for SessionMonitorExecutor
    <T> Optional<T> lockRootTaskIfExists(long attemptId, TaskLockActionWithDetails<T> func);

    interface SessionMonitorAction
    {
        Optional<Instant> schedule(StoredSessionMonitor monitor);
    }

    void lockReadySessionMonitors(Instant currentTime, SessionMonitorAction func);

    List<TaskRelation> getTaskRelations(long attemptId);

    List<Config> getExportParams(List<Long> idList);

    List<Config> getCarryParams(List<Long> idList);
}
