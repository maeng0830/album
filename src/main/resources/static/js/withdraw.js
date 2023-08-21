$('#memberWithdraw-form').submit(function (event) {
  event.preventDefault();

  var memberWithdrawForm = {
    password: $('#password').val(),
    checkedPassword: $('#checkedPassword').val()
  }

  $.ajax({
    type: 'DELETE',
    url: '/members',
    data: JSON.stringify(memberWithdrawForm),
    contentType: 'application/json',
    success: function (response) {
      // 응답 데이터가 AlbumException 타입인지 확인
      if (response.code && response.message) {
        alert(response.message);
      } else {
        alert("회원 탈퇴 되었습니다.");
        location.href = "/logout";
      }
    },
    error: function (xhr) {
      var errorMessage3 = xhr.responseText;
      alert(errorMessage3);
    }
  })
})