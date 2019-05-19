package smartspace.plugins;

import smartspace.data.ActionEntity;

public interface PluginCommand {
    public ActionEntity execute(ActionEntity actionEntity);

}
