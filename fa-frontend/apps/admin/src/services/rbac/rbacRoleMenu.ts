import { GATE_APP } from '@/configs/server.config';
import { BaseApi } from '@/services/base';
import * as Rbac from '../../../types/rbac';
import * as Fa from '@/../../../types/base/Fa';

/** ------------------------------------------ xx 操作接口 ------------------------------------------ */
class RbacRoleMenuApi extends BaseApi<Rbac.RbacRoleMenu, string> {
  /** 获取角色权限点 */
  getRoleMenu = (roleId: string): Promise<Fa.Ret<Rbac.RoleMenuVo>> => this.get(`getRoleMenu/${roleId}`);

  /** 更新角色权限点 */
  updateRoleMenu = (params: Rbac.RoleMenuVo): Promise<Fa.Ret> => this.post('updateRoleMenu', params);
}

export default new RbacRoleMenuApi(GATE_APP.rbac, 'rbacRoleMenu');
