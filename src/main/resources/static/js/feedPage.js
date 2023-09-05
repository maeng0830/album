// 피드 내용 불러오기
function getFeed(feedId, loginId) {
  console.log('getFeed 호출')
  $.ajax({

    type: 'GET',
    url: '/feeds/' + feedId,
    success: function (feed) {
      var feedHtml = '';
      var feedImageHtml = '';
      var buttonsHtml = '';

      var feedId = feed.id;
      var feedTitle = feed.title;
      var feedMemberId = feed.member.id;
      var feedMemberImage = feed.member.image != null ? feed.member.image.imageStoreName : '#';
      var feedMemberNickname = feed.member.nickname;
      var feedCreatedAt = feed.createdAt;
      var feedHits = feed.hits;
      var feedCommentCount = feed.commentCount;
      var feedImages = feed.feedImages;
      var feedContent = feed.content;

      // buttonsHtml 작성
      if (loginId === feedMemberId) {
        // 로그인한 사용자와 댓글 작성자의 memberId가 일치하는 경우
        buttonsHtml = `
              <button class="btn btn-sm btn-outline-primary me-2" onclick="location.href='/members/modified-feed/${feedId}';">수정</button>
              <button class="btn btn-sm btn-outline-danger" onclick="deleteFeedOrComment('feeds', ${feedId})">삭제</button>`;
      } else if (loginId !== -1) {
        // 로그인한 사용자와 댓글 작성자의 memberId가 일치하지 않는 경우
        buttonsHtml = `
              <button class="btn btn-sm btn-outline-danger" onclick="accuseTemplate('feeds', ${feedId})">신고</button>`;
      }

      // feedImageHtml 작성
      if (feedImages.length) {
        feedImageHtml += `<div class="carousel-inner">`;

        feedImages.forEach(feedImage => {
          var feedImageStoreName = feedImage.imageStoreName;
          var feedImageOriginalName = feedImage.imageOriginalName;

          feedImageHtml += `<div class="carousel-item active">
                            <img src="/images/${feedImageStoreName}" class="d-block w-100" alt="${feedImageOriginalName}">
                            </div>`;
        })

        feedImageHtml += `</div>
                          <button class="carousel-control-prev" type="button" data-bs-target="#carouselExampleControls" data-bs-slide="prev">
                            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                            <span class="visually-hidden">Previous</span>
                          </button>
                          <button class="carousel-control-next" type="button" data-bs-target="#carouselExampleControls" data-bs-slide="next">
                            <span class="carousel-control-next-icon" aria-hidden="true"></span>
                            <span class="visually-hidden">Next</span>
                          </button>`;
      }

      // feedHtml 작성
      feedHtml += `<!-- 피드 제목 -->
                  <div class="col-md-8 offset-md-2">
                      <h2 class="feed-title">${feedTitle}</h2>
                      <div class="d-flex align-items-center justify-content-end mb-3">
                        ${buttonsHtml}
                      </div>
                  </div>
                  <!-- 작성자 이미지 및 작성일자 -->
                  <div class="col-md-8 offset-md-2">
                    <div class="d-flex align-items-center justify-content-end mb-3">
                      <div class="avatar">
                        <a class="nav-link px-2 text-white dropdown-toggle" role="button" id="memberDropdown_${feedMemberId}" data-bs-toggle="dropdown" aria-expanded="false" href="#">
                          <img src="/images/${feedMemberImage}" alt="${feedMemberNickname}" class="img-fluid rounded-circle">
                        </a>
                        <ul class="dropdown-menu" aria-labelledby="memberDropdown_${feedMemberId}">
                            <li><p class="dropdown-item">${feedMemberNickname}</p></li>
                            <li><a class="dropdown-item" onclick="follow(${feedMemberId})">팔로우 하기</a></li>
                          </ul>
                      </div>
                      <div class="author-date ms-3">
                        ${feedCreatedAt}
                      </div>
                    </div>
                  </div>
                  <!-- 조회수 및 댓글수 -->
                  <div class="col-md-8 offset-md-2">
                    <div class="d-flex align-items-center justify-content-end mb-3">
                      <div class="hits">
                        ${feedHits}
                      </div>
                      <div class="comment-count ms-3">
                        댓글(${feedCommentCount})
                      </div>
                    </div>
                  </div>
                  <!-- 피드 이미지 -->
                  <div class="col-md-8 offset-md-2">
                      <div id="carouselExampleControls" class="carousel slide" data-bs-ride="carousel">
                        ${feedImageHtml}
                      </div>
                  </div>
                  <!-- 피드 내용 -->
                  <div class="col-md-8 offset-md-2">
                      <div class="feed-content">
                        <p>${feedContent}</p>
                      </div>
                  </div>`;

      document.querySelector('#feed-details').insertAdjacentHTML('beforeend', feedHtml);
    },
    error: function () {
      alert("피드 내용을 가져오는데 실패했습니다.")
    }
  })
}

// 댓글 목록 불러오기
function getComments(feedId, currentPage, loginId) {
  console.log('getComments 호출')
  let param = '?feedId=' + feedId + '&page=' + currentPage + '&size=10';

  $.ajax({
    type: 'GET',
    url: '/comments' + param,
    success: function (comments) {
      if (!comments.length) {
        alert('댓글이 없습니다.');
      } else {
        var commentsHtml = '';

        comments.forEach(group => {
          var buttonsHtml = '';

          var commentId = group.id;
          var memberId = group.member.id;
          var memberImage = group.member.image != null ? group.member.image.imageStoreName : '#';
          var memberNickname = group.member.nickname;
          var createdAt = group.createdAt;
          var content = group.content;
          var status = group.status;

          console.log(status);

          if (status !== 'DELETE') {
            if (loginId === memberId) {
              // 로그인한 사용자와 댓글 작성자의 memberId가 일치하는 경우
              buttonsHtml = `
              <button class="btn btn-sm btn-outline-primary me-2" onclick="modifiedCommentTemplate(${commentId}, '${content}')">수정</button>
              <button class="btn btn-sm btn-outline-danger" onclick="deleteFeedOrComment('comments', ${commentId})">삭제</button>`;
            } else if (loginId !== -1) {
              // 로그인한 사용자와 댓글 작성자의 memberId가 일치하지 않는 경우
              buttonsHtml = `
              <button class="btn btn-sm btn-outline-danger" onclick="accuseTemplate('comments', ${commentId})">신고</button>`;
            }
          }

          commentsHtml += `
                        <div class="comment">
                          <div class="comment-header d-flex align-items-center justify-content-between">
                            <div class="author-info d-flex align-items-center">
                              <div class="avatar">
                                <a class="nav-link px-2 text-white dropdown-toggle" role="button" id="memberDropdown_${memberId}" data-bs-toggle="dropdown" aria-expanded="false" href="#">
                                  <img src="/images/${memberImage}" alt="${memberNickname}" class="img-fluid rounded-circle">
                                </a>
                                <ul class="dropdown-menu" aria-labelledby="memberDropdown_${memberId}">
                                  <li><a class="dropdown-item" onclick="follow(${memberId})">팔로우 하기</a></li>
                                </ul>
                              </div>
                              <div class="author-name ms-3">
                                ${memberNickname}
                              </div>
                              <div class="separator mx-2">|</div>
                              <div class="comment-date">
                                ${createdAt}
                              </div>
                            </div>
                            <div class="actions">
                              ${buttonsHtml}
                            </div>
                          </div>
                          <div class="comment-body">
                            ${content}
                          </div>
                          <div>
                              <button class="comment-reply btn btn-sm btn-link me-2" onclick="commentReply(${feedId}, ${commentId}, ${commentId})">답글</button>
                          </div>
                          <hr class="my-4">
                        </div>`;

          group.basicComments.forEach(basic => {
            var buttonsHtml = '';

            var groupId = basic.groupId;
            var commentId = basic.id;
            var memberId = basic.member.id;
            var memberImage = basic.member.image != null ? basic.member.image.imageStoreName : '#';
            var memberNickname = basic.member.nickname;
            var createdAt = basic.createdAt;
            var content = basic.content;
            var parentNickname = basic.parentMember;
            var status = basic.status;

            if (status !== 'DELETE') {
              if (loginId === memberId) {
                // 로그인한 사용자와 댓글 작성자의 memberId가 일치하는 경우
                buttonsHtml = `
              <button class="btn btn-sm btn-outline-primary me-2" onclick="modifiedCommentTemplate(${commentId}, '${content}')">수정</button>
              <button class="btn btn-sm btn-outline-danger" onclick="deleteFeedOrComment('comments', ${commentId})">삭제</button>`;
              } else if (loginId !== -1) {
                // 로그인한 사용자와 댓글 작성자의 memberId가 일치하지 않는 경우
                buttonsHtml = `
              <button class="btn btn-sm btn-outline-danger" onclick="accuseTemplate('comments', ${commentId})">신고</button>`;
              }
            }

            commentsHtml += `
                        <div class="comment basic-comment">
                          <div class="comment-header d-flex align-items-center justify-content-between">
                            <div class="author-info d-flex align-items-center">
                              <div class="avatar">
                                <img src="/images/${memberImage}" alt="이미지" class="img-fluid rounded-circle">
                              </div>
                              <div class="author-name ms-3">
                                ${memberNickname}
                              </div>
                              <div class="separator mx-2">|</div>
                              <div class="comment-date">
                                ${createdAt}
                              </div>
                            </div>
                            <div class="actions">
                              ${buttonsHtml}
                            </div>
                          </div>
                            <div class="comment-body">
                              <em><b>${parentNickname}</b></em>
                              ${content}
                            </div>
                            <div>
                              <button class="comment-reply btn btn-sm btn-link me-2" onclick="commentReply(${feedId}, ${groupId}, ${commentId})">답글</button>
                            </div>
                          <hr class="my-4">
                        </div>`;
          })
        })

        if (currentPage === 1) {
          document.querySelector('#comment-list').innerHTML = commentsHtml;
        } else {
          document.querySelector('#comment-list').insertAdjacentHTML('beforeend', commentsHtml);
        }
      }
    },
    error: function () {
      alert("댓글 목록을 가져오는데 실패했습니다.");
    }
  })
}

// 댓글 등록
function postComment(feedId, content) {
  var commentPostForm = {
    feedId: feedId,
    content: content
  }

  $.ajax({
    type: 'POST',
    url: '/comments',
    data: JSON.stringify(commentPostForm),
    contentType: 'application/json',
    success: function (response) {
      // 응답 데이터가 AlbumException 타입인지 확인
      if (response.code && response.message) {
        alert(response.message);
      } else {
        alert("댓글 등록이 완료되었습니다.");
        location.reload();
      }
    },
    error: function () {
      alert("댓글 등록에 실패했습니다.");
    }
  })
}

// 답글 양식 모달
function commentReply(feedId, groupId, parentId) {
  console.log('commentReply 호출');

  var commentReplyHtml = `
                  <div class="modal-header p-5 pb-4 border-bottom-0">
                      <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                  </div>
                  <div class="modal-body p-5 pt-0">
                      <div class="form-floating mb-3">
                        <input type="hidden" class="form-control rounded-3" name="groupId" id="groupId" value="${groupId}">
                        <input type="hidden" class="form-control rounded-3" name="parentId" id="parentId" value="${parentId}">
                      </div>
                      <div class="form-floating mb-3">
                        <input type="text" class="form-control rounded-3" name="content" id="content" placeholder="답글 내용">
                        <label for="content">답글 내용</label>
                      </div>
                      <button class="w-100 mb-2 btn btn-lg rounded-3 btn-primary text-center" onclick="postCommentReply(${feedId}, ${groupId}, ${parentId}, $('#content').val())">답글 제출</button>
                   </div>`;

  console.log(commentReplyHtml);
  $('#commentReplyModal').html(commentReplyHtml);
  $('#commentReply').modal('show');
}

// 답글 등록
function postCommentReply(feedId, groupId, parentId, content) {
  var url = `/comments`;

  var jsonData = {
    feedId: feedId,
    groupId: groupId,
    parentId: parentId,
    content: content
  }

  $.ajax({
    type: 'POST',
    url: url,
    data: JSON.stringify(jsonData),
    contentType: 'application/json',
    success: function (response) {
      // 응답 데이터가 AlbumException 타입인지 확인
      if (response.code && response.message) {
        alert(response.message);
      } else {
        alert("답글 제출이 완료되었습니다.");
        location.reload();
      }
    },
    error: function () {
      alert("답글 제출이 실패했습니다.");
    }
  })
}

// 댓글 수정 모달
function modifiedCommentTemplate(id, prevContent) {
  console.log('modifiedTemplate 호출');

  var modifiedTemplateHtml = `
                  <div class="modal-header p-5 pb-4 border-bottom-0">
                      <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                  </div>
                  <div class="modal-body p-5 pt-0">
                      <div class="form-floating mb-3">
                        <input type="hidden" class="form-control rounded-3" name="domainId" id="domainId" value="${id}">
                      </div>
                      <div class="form-floating mb-3">
                        <input type="text" class="form-control rounded-3" name="content" id="content" value="${prevContent}">
                        <label for="content">수정 내용</label>
                      </div>
                      <button class="w-100 mb-2 btn btn-lg rounded-3 btn-primary text-center" onclick="putModifiedComment(${id}, $('#content').val())">댓글 수정</button>
                   </div>`;

  console.log(modifiedTemplateHtml);
  $('#modifiedCommentTemplateModal').html(modifiedTemplateHtml);
  $('#modifiedCommentTemplate').modal('show');
}

// 댓글 수정
function putModifiedComment(id, content) {
  var commentModifiedForm = {
    id: id,
    content: content
  }

  $.ajax({
    type: 'PUT',
    url: '/comments',
    data: JSON.stringify(commentModifiedForm),
    contentType: 'application/json',
    success: function (response) {
      // 응답 데이터가 AlbumException 타입인지 확인
      if (response.code && response.message) {
        alert(response.message);
      } else {
        alert("댓글 수정이 완료되었습니다.");
        location.reload();
      }
    },
    error: function () {
      alert("댓글 수정에 실패했습니다.");
    }
  })
}

// 피드 및 댓글 삭제
function deleteFeedOrComment(domain, id) {

  if (confirm("정말 삭제하시겠습니까?")) {
    $.ajax({
      type: 'DELETE',
      url: `/${domain}/${id}`,
      success: function (response) {
        // 응답 데이터가 AlbumException 타입인지 확인
        if (response.code && response.message) {
          alert(response.message);
        } else {
          alert("삭제 되었습니다.");
          if (domain == 'feed') {
            location.href = "/";
          } else {
            location.reload();
          }
        }
      },
      error: function () {
        alert("삭제 실패했습니다.");
      }
    })
  } else {
    return false;
  }
}

// 신고 양식 모달
function accuseTemplate(domain, id) {
  console.log('accuseTemplate 호출');

      var accuseTemplateHtml = `
                  <div class="modal-header p-5 pb-4 border-bottom-0">
                      <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                  </div>
                  <div class="modal-body p-5 pt-0">
                      <div class="form-floating mb-3">
                        <input type="hidden" class="form-control rounded-3" name="domainId" id="domainId" value="${id}">
                      </div>
                      <div class="form-floating mb-3">
                        <input type="text" class="form-control rounded-3" name="content" id="content" placeholder="신고 사유">
                        <label for="content">신고 사유</label>
                      </div>
                      <button class="w-100 mb-2 btn btn-lg rounded-3 btn-primary text-center" onclick="postAccuse('${domain}', ${id}, $('#content').val())">신고 제출</button>
                   </div>`;

  console.log(accuseTemplateHtml);
  $('#accuseTemplateModal').html(accuseTemplateHtml);
  $('#accuseTemplate').modal('show');
}

// 신고 등록
function postAccuse(domain, id, content) {
  var url = `/${domain}/${id}/accuse`;

  var jsonData = {
    content: content
  }

  $.ajax({
    type: 'PUT',
    url: url,
    data: JSON.stringify(jsonData),
    contentType: 'application/json',
    success: function (response) {
      // 응답 데이터가 AlbumException 타입인지 확인
      if (response.code && response.message) {
        alert(response.message);
      } else {
        alert("신고 제출이 완료되었습니다.");
        location.reload();
      }
    },
    error: function () {
      alert("신고 제출이 실패했습니다.");
    }
  })
}

// 팔로우 하기
function follow(followingId) {
  console.log('follow 호출');

  let url = `/follows/${followingId}`;

  $.ajax({
    type: 'POST',
    url: url,
    success: function (response) {
      if (response.code && response.message) {
        alert(response.message);
      } else {
        var follower = response.follower.nickname;
        var following = response.following.nickname;

        alert(`${follower}님이 ${following}님을 팔로우 했습니다.`);
      }
    },
    error: function () {
      alert("팔로우를 실패했습니다.");
    }
  })
}