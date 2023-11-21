// 서머노트
$(document).ready(function() {
  $('#content').summernote({
    placeholder: '내용을 입력하세요',
    height: 300
  });
})

function modifiedFeed(feedId, title, content, files) {
  var url = `/api/feeds`;

  var formData = new FormData();

  var feedModifiedForm = {
    id: feedId,
    title: title,
    content: content
  }

  var imageFiles = files[0].files;

  formData.append("feedModifiedForm", new Blob([JSON.stringify(feedModifiedForm)], {type: "application/json"}));

  for (var i = 0; i < imageFiles.length; i++) {
    formData.append("imageFiles", imageFiles[i]);
  }

  $.ajax({
    type: 'PUT',
    url: url,
    data: formData,
    contentType: false,
    processData: false,
    enctype: 'multipart/form-data',
    success: function (response) {
      if (response.code && response.message) {
        alert(response.message);
      } else {
        alert("피드가 수정되었습니다.")
        location.href = `/feed-page/${feedId}`;
      }
    },
    error: function (xhr) {
      var errorMessage3 = xhr.responseText;
      alert(errorMessage3);
    }
  })
}