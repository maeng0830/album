package com.maeng0830.album.member.dto.request;

import com.maeng0830.album.member.domain.MemberStatus;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberChangeStatusForm {

	@NotNull
	private Long id;
	private MemberStatus memberStatus;

	@Builder
	public MemberChangeStatusForm(Long id, MemberStatus memberStatus) {
		this.id = id;
		this.memberStatus = memberStatus;
	}
}
