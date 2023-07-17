// 기본 정보 수정
$('#basicInfo-form').submit(function (event) {
  event.preventDefault();

  var formData = new FormData();

  var memberModifiedForm = {
    nickname: $('#nickname').val(),
    phone: $('#phone').val(),
    birthDate: $('#birthDate').val()
  }

  var imageFile = $('#imageFile')[0].files[0];

  formData.append("memberModifiedForm", new Blob([JSON.stringify(memberModifiedForm)], {type: "application/json"}));
  formData.append("imageFile", imageFile);

  $.ajax({
    type: 'PUT',
    url: '/members',
    data: formData,
    contentType: false,
    processData: false,
    enctype: 'multipart/form-data',
    success: function (response) {
      if (response.code && response.message) {
       alert(response.message);
       return false
      }
      alert("회원 정보가 수정되었습니다.");
      location.href = "/";
    },
    error: function (xhr) {
      var errorMessage3 = xhr.responseText;
      alert(errorMessage3);
    }
  })
})

// 비밀번호 수정
$('#modPassword-form').submit(function (event) {
  event.preventDefault();

  var memberPasswordModifiedForm = {
    currentPassword: $('#currentPassword').val(),
    modPassword: $('#modPassword').val(),
    checkedModPassword: $('#checkedModPassword').val()
  }

  $.ajax({
    type: 'PUT',
    url: '/members/password',
    data: JSON.stringify(memberPasswordModifiedForm),
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
