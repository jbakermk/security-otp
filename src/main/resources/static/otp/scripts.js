function $(id) {
    return document.getElementById(id);
}

function visibility(id, visible) {
  $(id).style.display = visible ? 'block' : 'none';
}

function postJson(url, data, callback) {
    return fetch(url, {
        method: "POST",
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    }).then(response => callback(response)).catch(error => {
        console.error(
            "There has been a problem with your fetch operation:",
            error
        );
    });
}

['btnCancelRegister', 'btnCancelLogin', 'btnRegisterDone'].forEach(function(id) {
    $(id)?.addEventListener('click', (function () {
        window.location.replace("index.html");
    }));
});

$('btnRegister')?.addEventListener('click', function () {
    postJson("/otp/registration/" + $("login").value, {
        'password':$("password").value
    }, function(response) {
        if (response.ok) {
            response.text().then(function(text) {
                $('tokenQr').setAttribute("src", text);
            });
            new BSN.Modal('#modalRegister').show();
        }
    });
});

$('btnLogin')?.addEventListener('click', function () {
    visibility('msgLoginFailed', false);

    postJson('/otp/authenticate/'+$('login').value, {
        'password':$("password").value
    }, function(response) {
        if (response.ok) {
            response.json().then(function(text) {
                if (text === "REQUIRE_TOKEN_CHECK") {
                    new BSN.Modal('#modalLoginCheckToken').show();
                } else {
                    visibility('msgLoginFailed', true);
                }
            });
        } else {
            visibility('msgLoginFailed', true);
       }
    });
});

$('btnTokenVerify')?.addEventListener('click', function () {
    visibility('modalLoginCheckToken', false);

    postJson('/otp/authenticate/token/'+$('login').value, {
        'token':$("loginToken").value
    }, function(response) {
        if (response.ok) {
            response.json().then(function(text) {
                if (text === "AUTHENTICATED") {
                    window.location.replace("secured.html");
                } else {
                    visibility('msgTokenCheckFailed', false);
                }
            });
        } else {
           visibility('msgTokenCheckFailed', true);
        }
    });
});

$('btnLogout')?.addEventListener('click', function () {
    fetch('/otp/authenticate/logout', {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    }).then(response => {
        window.location.replace("index.html")
    }).catch(error => {
        console.error(
            "There has been a problem with your fetch operation:",
            error
        );
    });
});
