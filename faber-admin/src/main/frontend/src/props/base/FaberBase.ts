import {ReactNode} from "react";
import FaberEnums from "@/props/base/FaberEnums";
import {DragModalProps} from '@/components/modal/DragModal';
import ConditionQuery from '@/components/condition-query/interface';

namespace FaberBase {
  // ------------------------------------- Constant -------------------------------------
  export const Constant = {
    /** 约定：tree结构数据，根结点的ID默认为0 */
    TREE_SUPER_ROOT_ID: 0,
    TREE_SUPER_ROOT_LABEL: '根节点',
  }

  export const ROOT_DEFAULT = {
    value: FaberBase.Constant.TREE_SUPER_ROOT_ID,
    label: FaberBase.Constant.TREE_SUPER_ROOT_LABEL,
    isLeaf: false,
    hasChildren: true
  }

  // ------------------------------------- ENTITY -------------------------------------
	export interface BaseCrtEntity {
		/** 创建时间 */
		crtTime?: string;
		/** 创建人ID */
		crtUser?: string;
		/** 创建人 */
		crtName?: string;
		/** 创建IP */
		crtHost?: string;
	}

	export interface BaseOprEntity extends BaseCrtEntity {
		/** 更新时间 */
		updTime?: string;
		/** 更新人ID */
		updUser?: string;
		/** 更新人 */
		updName?: string;
		/** 更新IP */
		updHost?: string;
	}

	export interface BaseDelEntity extends BaseOprEntity {
		/** 删除状态 */
		delState?: FaberEnums.DelStateEnum;
	}

	// ------------------------------------- Http Request -------------------------------------
  /**
   * 服务返回数据定义
   */
  export interface Response<T = any> {
    /**
     * 状态码，默认200为成功
     */
    status: number;
    /**
     * 返回文本描述
     */
    message: string;
    /**
     * 返回主体内容
     */
    data: T;
  }

  export interface Dict {
    sort?: number;
    text: string;
    value: string;
    color?: string;
  }

  export interface PageDict {
    [key: string]: Dict[];
  }

  export interface Pagination {
    current: number;
    pageSize: number;
    total?: number;
  }

  export interface Page<T = any> {
    dicts: PageDict;
    pagination: Pagination;
    rows: T[];
  }

  /**
   * Antd Table排序
   */
  export interface Sorter {
    field: string;
    order: 'ascend' | 'descend' | null ;
  }

  /**
   * 通用entity新增、编辑弹框的属性
   */
  export interface CommonModalProps<T> extends DragModalProps {
    title?: string;
    record?: T;
    fetchFinish?: () => void;
  }

  /**
   * 通用查询组合参数
   */
  export interface InitQueryParams {
    /** 表格分页 */
    pagination?: Pagination;
    /** 排序 */
    sorter?: Sorter;
    /** 查询Form字段 */
    formValues?: any | undefined;
    /** 外部补充参数 */
    extraParams?: any;
    /** 场景ID */
    sceneId?: string | undefined;
    /** 组合查询 */
    conditionList?: ConditionQuery.CondGroup[];
  }

  /**
   * 通用查询组合参数
   */
  export interface QueryParams {
    /** 表格分页 */
    pagination: Pagination;
    /** 排序 */
    sorter: Sorter;
    /** 查询Form字段 */
    formValues: any | undefined;
    /** 外部补充参数 */
    extraParams?: any;
    /** 场景ID */
    sceneId?: string | undefined;
    /** 组合查询 */
    conditionList?: ConditionQuery.CondGroup[];
  }

	/**
	 * 分页参数
	 */
	export interface BasePageProps {
		sorter?: string; // 排序
		current?: number;
		pageSize?: number;
		/**
		 * 允许用户扩充 Item 字段
		 */
		[key: string]: any;
	}

  // ------------------------------------- Tree -------------------------------------
	/**
	 * 树节点位置变更[排序、父节点]
	 */
	export interface TreePosChangeVo {
		key: string | number;
		index: number;
		pid: string | number;
	}

	// ------------------------------------- 通用返回的Tree节点 -------------------------------------
	/**
	 * 通用返回的Tree节点
	 */
	export interface TreeNode<T = any, KeyType = string> {
		id: KeyType;
		parentId: KeyType;
		name: string;
		sort: number;
		hasChildren: boolean;
		children: TreeNode<T, KeyType>[] | undefined;
		sourceData: T;
	}

	export interface TreePathVo<T = any, KeyType = number> {
		list: TreeNode<T, KeyType>[];
		tree: TreeNode<T, KeyType>[];
	}

  /**
   * 运用于BaseTree的node节点
   */
  export interface BaseTreeNode<T = any, KeyType = number> {
    id?: KeyType;
    parentId?: KeyType;
    name?: string;
    // tree
    label: string;
    value: KeyType;
    isLeaf: boolean;
    children: TreeNode<T>[] | undefined;
    sourceData?: T;
  }

  // ------------------------------------- Layout -------------------------------------
  export enum ThemeStyle {
    light = 'light',
    dark = 'dark',
  }

  /** BaseChildProps */
  export interface BaseChildProps {
    children?: ReactNode;
  }

}

export default FaberBase;
