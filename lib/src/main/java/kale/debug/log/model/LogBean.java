package kale.debug.log.model;

import java.io.Serializable;

import kale.debug.log.constant.Level;


/**
 * @author Kale
 * @date 2016/3/25
 */
public class LogBean implements Serializable {

    public String tag, msg, time;

    public Level lev;

}
