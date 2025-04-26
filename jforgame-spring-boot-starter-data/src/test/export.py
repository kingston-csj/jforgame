import math

import pandas as pd
import json
import os
import glob
import hashlib

fileMd5 = {}
source_directory = 'your excel source path'
target_json_directory = "your json saving path"
target_typescript_directory = "your typescript code saving path"


def calculate_md5(text):
    """
    计算给定字符串的MD5值
    """
    md5_hash = hashlib.md5()
    md5_hash.update(text.encode('utf-8'))
    return md5_hash.hexdigest()


def excel_to_json(excel_path):
    file_name_without_ext = os.path.splitext(os.path.basename(excel_path))[0]
    df = pd.read_excel(excel_path)
    type_row = df.iloc[0]  # 获取第二行（类型说明行）
    header_row = df.iloc[1]  # 获取第三行（属性名行）
    dataRowIndex = 2
    export_header = {}
    # 如果第三行第一列为EXPORT，表示包含导出类型
    if header_row.iloc[0] == 'EXPORT':
        export_header = df.iloc[1]
        header_row = df.iloc[2]
        dataRowIndex = 3
    result = []
    for index, row in df[dataRowIndex:].iterrows():  # 从第四行开始处理实际数据
        item = {}
        try:
            for col_idx, col in enumerate(df.columns):
                if col_idx == 0:
                    continue
                if pd.isna(type_row[col]):
                    continue
                export_type = "CLIENT"
                if col in export_header:
                    export_type = str(export_header[col])
                if export_type.upper() == 'CLIENT' or export_type.upper() == 'BOTH':
                    col_type = type_row[col]
                    col_name = header_row[col]
                    value = row[col]
                    if 'int' in str(col_type):
                        item[col_name] = int(value) if pd.notnull(value) else 0
                    elif 'float' in str(col_type):
                        item[col_name] = float(value) if pd.notnull(value) else 0
                    elif 'str' in str(col_type) or 'string' in str(col_type):
                        item[col_name] = value if pd.notnull(value) else None
                    elif 'date' in str(col_type):
                        item[col_name] = value.strftime("%Y-%m-%d %H:%M:%S") if pd.notnull(value) else None
                    elif 'list' in str(col_type):
                        try:
                            item[col_name] = json.loads(value) if pd.notnull(value) else None
                        except json.JSONDecodeError:
                            item[col_name] = value
                    elif 'json' in str(col_type):
                        try:
                            item[col_name] = json.loads(value) if pd.notnull(value) else None
                        except json.JSONDecodeError:
                            item[col_name] = value
                    else:
                        item[col_name] = value
        except Exception as e:
            print(e)
            print(f"解析{excel_path}出错")
        result.append(item)

    generate_code_from_json(type_row, header_row, result[0], file_name_without_ext,
                            os.path.join(target_typescript_directory, f"Config_{file_name_without_ext}_item.ts"))

    return result


def type2TypeScript(type):
    if "int" in type:
        return "number"
    elif "str" in type:
        return "string"
    return "json"


def generate_code_from_json(typeRow, headerRow, dataRow, class_name, target_code_path):
    """
    根据JSON数据结构生成对应代码内容并写入代码文件
    """

    # 生成主配置类代码
    config_class_code = f'''
    import AbsConfigItem from "../../config/AbsConfigItem";
        '''

    field_lists = []
    firstColumn = False
    for key, value in headerRow.items():
        if not (firstColumn):
            firstColumn = True
            continue
        if pd.isnull(typeRow[key]):
            continue
        field_type = typeRow[key]
        ts_type = type2TypeScript(field_type)
        if ts_type == "json":
            innerClassName = headerRow[key] + "Def"
            innerClassName = innerClassName[0].upper() + innerClassName[1:]
            config_class_code += f"\nexport class {innerClassName} {{\n"
            for _key, _value in dataRow[headerRow[key]][0].items():
                value_type = type2TypeScript(type(_value).__name__)
                config_class_code += f"    public {_key}: {value_type};\n"
            config_class_code += "}\n"

            ts_type = f"Array<{innerClassName}>"
        field_lists.append({
            "name": headerRow[key],
            "type": ts_type
        })

    config_class_code += f'''
    export default class Config_{class_name}_item extends AbsConfigItem {{
      public static file_name:string = "{class_name}";
    '''
    for field in field_lists:
        config_class_code += f'''
        private _{field["name"]}: {field["type"]};
        public get {field["name"]}():{field["type"]} {{return this._{field["name"]};}}
        '''
    config_class_code += f'''
    public constructor(data:any) {{
        super(data);
    '''
    for field in field_lists:
        config_class_code += f"        this._{field['name']} = data['{field['name']}']\n"
    config_class_code += "    }\n}\n"

    # 检查excel表定义发生变化，生成的代码也就不一样
    # 如果属于新文件，或者跟记录的md5不一致，则生成新的文件
    newMd5 = calculate_md5(config_class_code)
    if not (class_name in fileMd5) or fileMd5[class_name] != newMd5:
        with open(target_code_path, 'w', encoding='utf-8') as f:
            f.write(config_class_code)
            print(f"Code file has been successfully generated for file {class_name}")

    fileMd5[class_name] = newMd5


if __name__ == "__main__":
    # 定义生成代码文件的目标目录，按需修改
    excel_files = glob.glob(os.path.join(source_directory, "*.xlsx"))

    md5_file_path = "file_md5.json"
    if os.path.exists(md5_file_path):
        with open(md5_file_path, 'r', encoding='utf-8') as f:
            fileMd5 = json.load(f)

    for excel_file in excel_files:
        json_data = excel_to_json(excel_file)
        # ensure_ascii表示禁止中文转义
        json_str = json.dumps(json_data, indent=4, ensure_ascii=False)

        file_name_without_ext = os.path.splitext(os.path.basename(excel_file))[0]
        json_file_path = os.path.join(target_json_directory, file_name_without_ext + ".json")

        with open(json_file_path, 'w', encoding='utf-8') as f:
            f.write(json_str)
        print(f"JSON data has been successfully written to {json_file_path} for file {excel_file}")

    with open(md5_file_path, 'w', encoding='utf-8') as f:
        json.dump(fileMd5, f)
