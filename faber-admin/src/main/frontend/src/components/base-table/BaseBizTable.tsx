import React, {createContext, useEffect, useState} from 'react';
import {find, get, sumBy} from 'lodash';
import {ClearOutlined, DeleteOutlined, SettingOutlined} from '@ant-design/icons';
import {Button, Modal, Space, Table} from 'antd';
import TableColConfigModal from '../modal/TableColConfigModal';
import FaberTable from './FaberTable';
import {useWindowSize} from 'react-use';
import {showResponse} from '@/utils/utils';
import {dataIndexToString} from './utils';
import ComplexQuery from '@/components/condition-query/ComplexQuery'
import {TableRowSelection} from "antd/es/table/interface";

interface CProps {
  localData: boolean; // 是否本地数据[查询场景、字段配置]
}

export const BaseBizTableContext = createContext<CProps>({
  localData: false,
});

/**
 * 基础业务表格组件
 * 1. 带字段自定义配置展示功能
 */
export default function BaseBizTable<RecordType extends object = any>({
  showTableColConfigBtn = true,
  showComplexQuery = true,
  showCheckbox = true,
  localData = false,
  buzzModal = '',
  columns,
  refreshList,
  batchDelete,
  renderQuerySuffix = () => null,
  renderCheckBtns = (rowKeys: any[]) => null,
  onSceneChange = () => {},
  onConditionChange = () => {},
  rowSelection,
  rowClickSelected,
  rowClickSingleSelected = true,
  onSelectedRowsChange,
  showBatchBelBtn = true,
  showTopTips,
  scrollYOccupied = 273,
  scrollY,
  keyName = "id",
  ...props
}: FaberTable.BaseTableProps<RecordType>) {
  const [config, setConfig] = useState<FaberTable.ColumnsProp<RecordType>[]>();
  const [innerScrollY, setInnerScrollY] = useState(document.body.clientHeight - scrollYOccupied)
  const {width, height} = useWindowSize();

  useEffect(() => {
    if (scrollY) {
      setInnerScrollY(scrollY)
    } else {
      setInnerScrollY(document.body.clientHeight - scrollYOccupied);
    }
  }, [scrollY, height, scrollYOccupied])

  const [selectedRowKeys, setSelectedRowKeys] = useState<any[]>([]);
  const [batchDeleting, setBatchDeleting] = useState(false)

  useEffect(() => {
    setSelectedRowKeys([]);
  }, [get(props, 'pagination.total'), get(props, 'pagination.current'), get(props, 'pagination.pageSize')]);

  /**
   * 解析表格自定义配置
   * @return { parseColumns, scrollWidthX }
   * parseColumns 解析用户配置解析后的自定义字段配置
   * scrollWidthX 解析表格宽度
   */
  const processColumns = () => {
    // 表格字段配置
    // 解析自定义配置
    let parseColumns = [];
    if (config) {
      // 取自定义配置
      parseColumns = config.map((c) => {
        const col = find(columns, (d) => dataIndexToString(d.dataIndex) === dataIndexToString(c.dataIndex));
        if (c.width) {
          return { ...col, ...c, width: Number(c.width) };
        }
        return { ...col, ...c };
      });
    } else {
      // 取默认值
      parseColumns = columns.filter((c) => c.tcRequired || c.tcChecked);
    }

    // 计算table滚动width
    const scrollWidthX = sumBy(parseColumns, (n) => Number(n.width) || 200);

    return { parseColumns, scrollWidthX };
  };

  /** 表格配置变更 */
  function handleTableColConfigChange(tableColumns: FaberTable.ColumnsProp<RecordType>[]) {
    const c = tableColumns.filter((col) => col.tcRequired || col.tcChecked);
    setConfig(c);
  }

  /** 批量删除Item */
  function handleBatchDelete() {
    Modal.confirm({
      title: '删除',
      content: `确认删除勾选中的 ${selectedRowKeys.length} 条数据？`,
      okText: '删除',
      okType: 'danger',
      onOk: async (close) => {
        if (batchDelete) {
          setBatchDeleting(true)
          batchDelete(selectedRowKeys).then((res) => {
            setBatchDeleting(false)
            showResponse(res, '批量删除');
            refreshList();
            close();
          }).catch(() => setBatchDeleting(false))
        }
      },
    });
  }

  const { parseColumns, scrollWidthX } = processColumns();

  const myRowSelection: TableRowSelection<RecordType> = {
    fixed: true,
    selectedRowKeys,
    onChange: (rowKeys) => {
      updateRowKeys(rowKeys);
    },
    ...rowSelection,
    // columnWidth: 30,
  };

  function updateRowKeys(rowKeys: any[]) {
    setSelectedRowKeys(rowKeys);
    if (onSelectedRowsChange) {
      onSelectedRowsChange(rowKeys);
    }
  }

  return (
    <BaseBizTableContext.Provider value={{ localData }}>
      <div>
        <div>
          {/* 多选删除 */}
          {selectedRowKeys.length > 0 && (
            <Space style={{ padding: 8, display: 'flex', lineHeight: '32px' }}>
              <div style={{ marginRight: 12 }}>
                已选中&nbsp;<a>{selectedRowKeys.length}</a>&nbsp;条数据
              </div>
              {renderCheckBtns && renderCheckBtns(selectedRowKeys)}
              {showBatchBelBtn && (
                <Button loading={batchDeleting} onClick={handleBatchDelete} icon={<DeleteOutlined />} danger>
                  删除
                </Button>
              )}
              <Button onClick={() => updateRowKeys([])} icon={<ClearOutlined />}>
                取消选中
              </Button>
            </Space>
          )}
          {/* 高级组合查询 */}
          {selectedRowKeys.length === 0 && (
            <div style={{ padding: 8, display: 'flex', alignItems: 'center' }}>
              {showComplexQuery && <ComplexQuery columns={columns} buzzModal={buzzModal} onSceneChange={onSceneChange} onConditionChange={onConditionChange} />}
              <div style={{ flex: 1 }}>{renderQuerySuffix && renderQuerySuffix()}</div>
              <div style={{ lineHeight: '32px' }}>
                共<a style={{ fontWeight: 600, margin: '0 4px' }}>{get(props, 'pagination.total')}</a>条数据
              </div>
            </div>
          )}
        </div>
        <div style={{ position: 'relative' }}>
          {/* eslint-disable-next-line react/jsx-props-no-spreading */}
          <Table
            columns={parseColumns}
            rowSelection={showCheckbox ? myRowSelection : undefined}
            scroll={{ x: scrollWidthX, y: innerScrollY }}
            onRow={(record) => ({
              onClick: () => {
                if (!rowClickSelected) return;
                const clickId = get(record, 'id');
                let newRowKey = [];
                if (rowClickSingleSelected) {
                  newRowKey = [clickId];
                } else {
                  if (selectedRowKeys.indexOf(clickId) > -1) {
                    newRowKey = selectedRowKeys.filter((i) => i === clickId);
                  } else {
                    newRowKey = [...selectedRowKeys, get(record, keyName)];
                  }
                }
                setSelectedRowKeys(newRowKey);
                if (onSelectedRowsChange) {
                  onSelectedRowsChange(newRowKey);
                }
              },
            })}
            size="small"
            {...props}
          />
          {/* 表格自定义配置 */}
          {showTableColConfigBtn ? (
            <div style={{ position: 'absolute', right: 4, top: 4, zIndex: 9 }}>
              <TableColConfigModal columns={columns} buzzModal={buzzModal} buzzName="表格字段展示配置" onConfigChange={handleTableColConfigChange}>
                <Button icon={<SettingOutlined />} type="text" />
              </TableColConfigModal>
            </div>
          ) : null}
        </div>
      </div>
    </BaseBizTableContext.Provider>
  );
}
