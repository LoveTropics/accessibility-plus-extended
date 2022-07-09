package net.shoaibkhan.accessibiltyplusextended.util;

import net.minecraft.client.KeyMapping;

public enum KeyBinds {
    CONFIG_KEY(null),
    AP_CONFIG_KEY(null),
    LockEntityKey(null),
    LEFT_KEY(null),
    RIGHT_KEY(null),
    UP_KEY(null),
    DOWN_KEY(null),
    GROUP_KEY(null),
    HOME_KEY(null),
    END_KEY(null),
    CLICK_KEY(null),
    RIGHT_CLICK_KEY(null);

    private KeyMapping keyBind;


    KeyBinds(KeyMapping keyBind) {this.keyBind = keyBind;}

    public KeyMapping getKeyBind(){return this.keyBind;}

    public void setKeyBind(KeyMapping newKeyBind){this.keyBind = newKeyBind;}
}
