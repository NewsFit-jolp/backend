package com.example.newsfit.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 49716410L;

    public static final QMember member = new QMember("member1");

    public final com.example.newsfit.global.entity.QBaseEntity _super = new com.example.newsfit.global.entity.QBaseEntity(this);

    public final DateTimePath<java.util.Date> birth = createDateTime("birth", java.util.Date.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath email = createString("email");

    public final EnumPath<Gender> gender = createEnum("gender", Gender.class);

    //inherited
    public final BooleanPath isDeleted = _super.isDeleted;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final NumberPath<Long> member_id = createNumber("member_id", Long.class);

    public final StringPath nickname = createString("nickname");

    public final StringPath phone = createString("phone");

    public final ListPath<Categories, EnumPath<Categories>> preferredCategories = this.<Categories, EnumPath<Categories>>createList("preferredCategories", Categories.class, EnumPath.class, PathInits.DIRECT2);

    public final StringPath profileImage = createString("profileImage");

    public final EnumPath<Role> role = createEnum("role", Role.class);

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

