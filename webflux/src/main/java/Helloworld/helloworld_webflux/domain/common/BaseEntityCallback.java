package Helloworld.helloworld_webflux.domain.common;

import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BaseEntityCallback implements BeforeConvertCallback<BaseEntity> {
    //  @CreatedDate와 @LastModifiedDate 사용 불가능. -> 생성 및 수정 시간을 수동 으로 등록 하는 코드
    @Override
    public BaseEntity onBeforeConvert(BaseEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
        }
        entity.setUpdatedAt(now);
        return entity;
    }
}
