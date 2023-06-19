package com.back.moment.photos.repository.getPhotoWhoLove;

import java.util.List;
import java.util.Map;

public interface GetPhotoWhoLove {
    Map<Long, Boolean> findPhotoLoveMap(List<Long> photoIdList, Long usersId);
}
