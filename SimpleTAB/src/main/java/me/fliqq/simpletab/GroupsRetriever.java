package me.fliqq.simpletab;

import java.util.Collection;
import java.util.Collections;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;

public class GroupsRetriever {
    private final LuckPerms luckPerms;
    private final SimpleTAB plugin = SimpleTAB.getInstance();

    public GroupsRetriever(LuckPerms luckPerms){
        if (luckPerms == null) {
            throw new IllegalArgumentException("LuckPerms instance cannot be null");
        }
        this.luckPerms = luckPerms;
        }

    public Collection<Group> getAllGroups() {
        GroupManager groupManager = luckPerms.getGroupManager();

        if (groupManager == null) {
            plugin.getLogger().severe("GroupManager is null. Ensure LuckPerms is correctly initialized.");
            return Collections.emptyList();
        }

        Collection<Group> groups = groupManager.getLoadedGroups();
        if (groups == null || groups.isEmpty()) {
            plugin.getLogger().info("No groups are currently loaded.");
            return Collections.emptyList();
        }

        return groups;
    }
}
