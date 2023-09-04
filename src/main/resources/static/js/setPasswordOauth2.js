$('#oauth2Password-form').submit(function (event) {
  event.preventDefault();

  var oauth2PasswordForm = {
    password: $('#password').val(),
    checkedPassword: $('#checkedPassword').val()
  }

  $.ajax({
    type: 'PUT',
    url: '/members/oauth2-password',
    data: JSON.stringify(oauth2PasswordForm),
    contentType: 'application/json',
    success: function (response) {
      // 응답 데이터가 AlbumException 타입인지 확인
      if (response.code && response.message) {
        alert(response.message);
      } else {
        alert("비밀번호가 수정되었습니다. 다시 로그인 해주세요.");
        location.href = "/logout";
      }
    },
    error: function (xhr) {
      var errorMessage3 = xhr.responseText;
      alert(errorMessage3);
    }
  })
})