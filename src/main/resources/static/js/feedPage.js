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
        var buttonsHtml = '';

        comments.forEach(group => {
          var memberId = group.member.id;
          var memberImage = group.image != null ? group.image.imagePath : '#';
          var memberNickname = group.member.nickname;
          var createdAt = group.createdAt;
          var content = group.content;

          if (loginId === memberId) {
            // 로그인한 사용자와 댓글 작성자의 memberId가 일치하는 경우
            buttonsHtml = `
              <button class="btn btn-sm btn-outline-primary me-2">수정</button>
              <button class="btn btn-sm btn-outline-danger">삭제</button>`;
          } else if (loginId !== -1) {
            // 로그인한 사용자와 댓글 작성자의 memberId가 일치하지 않는 경우
            buttonsHtml = `
              <button class="btn btn-sm btn-outline-danger">신고</button>`;
          }

          commentsHtml += `
                        <div class="comment">
                          <div class="comment-header d-flex align-items-center justify-content-between">
                            <div class="author-info d-flex align-items-center">
                              <div class="avatar">
                                <img src="${memberImage}" alt="이미지" class="img-fluid rounded-circle">
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
                          <hr class="my-4">
                        </div>`;

          group.basicComments.forEach(basic => {
            var memberId = basic.member.id;
            var memberImage = basic.image != null ? group.image.imagePath : '#';
            var memberNickname = basic.member.nickname;
            var createdAt = basic.createdAt;
            var content = basic.content;
            var parentNickname = basic.parentMember;

            if (loginId === memberId) {
              // 로그인한 사용자와 댓글 작성자의 memberId가 일치하는 경우
              buttonsHtml = `
              <button class="btn btn-sm btn-outline-primary me-2">수정</button>
              <button class="btn btn-sm btn-outline-danger">삭제</button>`;
            } else if (loginId !== -1) {
              // 로그인한 사용자와 댓글 작성자의 memberId가 일치하지 않는 경우
              buttonsHtml = `
              <button class="btn btn-sm btn-outline-danger">신고</button>`;
            }

            commentsHtml += `
                        <div class="comment basic-comment">
                          <div class="comment-header d-flex align-items-center justify-content-between">
                            <div class="author-info d-flex align-items-center">
                              <div class="avatar">
                                <img src="${memberImage}" alt="이미지" class="img-fluid rounded-circle">
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

function getFeed(feedId, loginId) {
  console.log('getFeed 호출')
  $.ajax({

    type: 'GET',
    url: '/feeds/' + feedId,
    success: function (feed) {
      var feedHtml = '';
      var feedImageHtml = '';
      var buttonsHtml = '';

      var feedTitle = feed.title;
      var feedMemberId = feed.member.id;
      var feedMemberImage = feed.member.image != null ? feed.member.image.imagePath : '#';
      var feedCreatedAt = feed.createdAt;
      var feedHits = feed.hits;
      var feedCommentCount = feed.commentCount;
      var feedImages = feed.feedImages;
      var feedContent = feed.content;

      // buttonsHtml 작성
      if (loginId === feedMemberId) {
        // 로그인한 사용자와 댓글 작성자의 memberId가 일치하는 경우
        buttonsHtml = `
              <button class="btn btn-sm btn-outline-primary me-2">수정</button>
              <button class="btn btn-sm btn-outline-danger">삭제</button>`;
      } else if (loginId !== -1) {
        // 로그인한 사용자와 댓글 작성자의 memberId가 일치하지 않는 경우
        buttonsHtml = `
              <button class="btn btn-sm btn-outline-danger">신고</button>`;
      }

      // feedImageHtml 작성
      if (feedImages.length) {
        feedImageHtml += `<div class="carousel-inner">`;

        feedImages.forEach(feedImage => {
          var feedImagePath = feedImage.imagePath;
          var feedImageName = feedImage.imageOriginalName;

          feedImageHtml += `<div class="carousel-item active">
                            <img src="${feedImagePath}" class="d-block w-100" alt="${feedImageName}">
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
                        <img src="${feedMemberImage}" alt="이미지" class="img-fluid rounded-circle">
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