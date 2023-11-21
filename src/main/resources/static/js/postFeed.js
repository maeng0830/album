// 서머노트
$(document).ready(function() {
  $('#content').summernote({
    placeholder: '내용을 입력하세요',
    height: 300
  });
})


// 피드 등록
$('#feed-form').submit(function (event) {
  event.preventDefault();

  var formData = new FormData();

  var feedPostForm = {
    title: $('#title').val(),
    content: $('#content').val()
  }

  var imageFiles = $('#imageFiles')[0].files;

  formData.append("feedPostForm", new Blob([JSON.stringify(feedPostForm)], {type: "application/json"}));

  for (var i = 0; i < imageFiles.length; i++) {
    formData.append("imageFiles", imageFiles[i]);
  }

  $.ajax({
    type: 'POST',
    url: '/api/feeds',
    data: formData,
    contentType: false,
    processData: false,
    enctype: 'multipart/form-data',
    success: function (response) {
      if (response.code && response.message) {
        alert(response.message);
      } else {
        alert("피드가 등록되었습니다.")
        location.href = `/`;
      }
    },
    error: function (xhr) {
      var errorMessage3 = xhr.responseText;
      alert(errorMessage3);
    }
  })
})
