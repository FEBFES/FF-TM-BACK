package com.febfes.fftmback.domain.common.specification;

import com.febfes.fftmback.domain.dao.TaskView;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.EqualIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.JoinFetch;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@JoinFetch(paths = "owner", alias = "own")
@JoinFetch(paths = "assignee", alias = "asg")
@JoinFetch(paths = "taskType", alias = "tt")
@And({
        @Spec(path = "id", params = "id", spec = Equal.class),
        @Spec(path = "name", params = "name", spec = LikeIgnoreCase.class),
        @Spec(path = "description", params = "description", spec = LikeIgnoreCase.class),
        @Spec(path = "own.id", params = "ownerId", spec = Equal.class),
        @Spec(path = "asg.id", params = "assigneeId", spec = Equal.class),
        @Spec(path = "priority", params = "priority", spec = EqualIgnoreCase.class),
        @Spec(path = "tt.name", params = "taskType", spec = Equal.class)
})
public interface TaskSpec extends Specification<TaskView> {

    static Specification<TaskView> byColumnId(Long columnId) {
        return (root, query, builder) -> builder.equal(root.get("columnId"), columnId);
    }
}
