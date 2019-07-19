package com.jamen.service;

import com.jamen.model.FriendLink;
import com.jamen.model.Result;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author: jamen
 * @Date: 2019/5/16 17:08
 * Describe:
 */
public interface FriendLinkService {

    Result addFriendLink(FriendLink friendLink);

    JSONArray getAllFriendLink();

    Result updateFriendLink(FriendLink friendLink, int id);

    Result deleteFriendLink(int id);

    Result getFriendLink();
}
