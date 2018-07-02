/**
 * Databinding Sample
 * 2018-05-01 K.OHWADA
 */
package jp.ohwada.android.databindingsample;

import android.databinding.BaseObservable;

import android.databinding.Bindable;

/**
 * class User
 * original : https://qiita.com/t_sakaguchi/items/a83910a990e64f4dbdf1
 */
public class User  extends BaseObservable {

    private String name;


/**
 * constractor
 */
    public User(String name){
        this.name = name;
    } //  User


/**
 * getName
 * Bindable create BR.name
 */
    @Bindable
    public String getName() {
        return name;
    } // getName


/**
 * setName
 */
    public void setName(String name) {
        this.name = name;
      notifyPropertyChanged(BR.name);
    } // setName


} // class User
