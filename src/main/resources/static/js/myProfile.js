$('#basicInfo-form').submit(function (event) {
  event.preventDefault();

  var formData = new FormData();

  var memberModifiedForm = {
    nickname: $('#nickname').val(),
    phone: $('#phone').val()
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
      alert("회원 정보가 수정되었습니다.");
      location.href = "/";
    },
    error: function (xhr) {
      var errorMessage3 = xhr.responseText;
      alert(errorMessage3);
    }
  })
})
