/**
 * Created by Administrator on 2018/7/19.
 */
/***
 * if(this.value==''){this.value='用户名'}
 */
function onblurName() {
    var username = document.getElementById("username");
    if(username == ""){
        username.innerHTML = "用户名";
    }
}

function onfocusName() {
    var username = document.getElementById("username");
    username.innerHTML = "";
}
