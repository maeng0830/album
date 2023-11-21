// 기본 정보 수정
$('#signup-form').submit(function (event) {
  console.log('signup-form 호출');
  event.preventDefault();

  var memberJoinForm = {
    username: $('#username').val(),
    nickname: $('#nickname').val(),
    password: $('#password').val(),
    checkedPassword: $('#checkedPassword').val(),
    phone: $('#phone').val()
  }

  $.ajax({
    type: 'POST',
    url: '/api/members',
    data: JSON.stringify(memberJoinForm),
    contentType: 'application/json',
    success: function (response) {
      if (response.code && response.message) {
        alert(response.message);
        return false;
      }

      alert("회원 가입되었습니다.");
      location.href = "/";
    },
    error: function (xhr) {
      var errorMessage3 = xhr.responseText;
      alert(errorMessage3);
    }
  })
})