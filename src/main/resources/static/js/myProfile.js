$(document).ready(function () {
  $('#basicInfo-form').submit(function (e) {
    e.preventDefault();

    var formData = new FormData();

    var memberModifiedForm = {
      nickname: $('#nickname').val(),
      phone: $('#phone').val()
    }

    var imageFile = $('#imageFile')[0].files[0];

    formData.append("memberModifiedForm", new Blob([JSON.stringify(memberModifiedForm)], {type: "application/json"}));
    formData.append("imageFile", imageFile);

    $.ajax({
      type: 'POST',
      url: '/members',
      data: formData,
      contentType: false,
      processData: false,
      enctype: 'multipart/form-data',
      success: function (response) {
        alert("회원 정보가 수정되었습니다.");
        location.href = "/";
      },
      error: function (error) {
        alert(JSON.stringify(error));
      }
    })
  })
})