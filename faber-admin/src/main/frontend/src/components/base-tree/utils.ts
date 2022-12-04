import {get, isNil, trim} from 'lodash';
import Fa from '@/props/base/Fa';
import BaseTreeProps from './interface';

export function parseNode<T = any>(nodeList: Fa.TreeNode<T, any>[] | undefined): BaseTreeProps.TreeNode<T>[] | undefined {
	if (isNil(nodeList) || nodeList.length === 0) return undefined;
	return nodeList.map((d) => ({
		id: d.id,
		parentId: d.parentId,
		name: d.name,
		// tree
		label: d.name,
		value: d.id,
		isLeaf: !d.hasChildren,
		children: parseNode<T>(d.children),
		sourceData: d.sourceData,
	}));
}

/** 平铺Tree型结构 */
export function flatTreeList(tree: BaseTreeProps.NodeProps[] = [], pid: string | number = 0): BaseTreeProps.FlatTreeNode[] {
	const list: BaseTreeProps.FlatTreeNode[] = [];
	tree.forEach((item, index) => {
		const { children, key, name } = item;
		if (children && children[0]) {
			list.push(...flatTreeList(children, item.key));
		}
		list.push({ key, index, pid, name });
	});
	return list;
}

function findPathInner(options: any[] | undefined, destId: any, valueKey = 'value'): any {
	if (isNil(options)) return undefined;
	for (let i = 0; i < options.length; i += 1) {
		const o = options[i];
		// first check self is desc
		if (trim(get(o, valueKey)) === trim(destId)) {
			return [o];
		}
		if (o.children && o.children[0]) {
			// try find in children
			const co = findPathInner(o.children, destId, valueKey);
			if (co) {
				return [o, ...co];
			}
		}
	}
	return undefined;
}

/**
 * 从options中查找目标值的路径数组
 * @param options
 * @param destId
 * @param valueKey
 */
export function findPath(options: any[] | undefined, destId: any, valueKey = 'value') {
	return findPathInner(options, destId, valueKey) || [];
}
