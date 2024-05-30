package com.febfes.fftmback.domain.common.specification;

import com.febfes.fftmback.domain.dao.TaskView;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.EqualIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.JoinFetch;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

@JoinFetch(paths = "owner", alias = "own")
@JoinFetch(paths = "assignee", alias = "asg")
@JoinFetch(paths = "taskType", alias = "tt")
@And({
        @Spec(path = "id", params = "taskId", spec = Equal.class),
        @Spec(path = "name", params = "taskName", spec = LikeIgnoreCase.class),
        @Spec(path = "description", params = "taskDescription", spec = LikeIgnoreCase.class),
        @Spec(path = "own.id", params = "taskOwnerId", spec = Equal.class),
        @Spec(path = "asg.id", params = "taskAssigneeId", spec = Equal.class),
        @Spec(path = "priority", params = "taskPriority", spec = EqualIgnoreCase.class),
        @Spec(path = "tt.name", params = "taskType", spec = Equal.class)
})
public interface TaskSpec extends Specification<TaskView> {

    static Specification<TaskView> byColumnId(Long columnId) {
        return (root, query, builder) -> builder.equal(root.get("columnId"), columnId);
    }

    static Specification<TaskView> columnIdIn(Set<Long> columnIds) {
        return (root, query, builder) -> root.get("columnId").in(columnIds);
    }
}
