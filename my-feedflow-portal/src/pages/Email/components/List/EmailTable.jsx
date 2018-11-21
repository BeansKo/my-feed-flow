import React, { Component } from 'react';
import { Table, Pagination, Dialog, Feedback  } from '@icedesign/base';
import IceContainer from '@icedesign/container';
import IceImg from '@icedesign/img';
import PropTypes from 'prop-types';

export default class EmailTable extends Component {
  static displayName = 'EmailTable';

  static propTypes = {
    list : PropTypes.func.isRequired,
    del : PropTypes.func.isRequired,
    edit : PropTypes.func.isRequired,
  };

  static defaultProps = {};

  constructor(props) {
    super(props);
    this.state = {
      loading : true,
      data : [],
      currentPage : 1,
      pageSize : 10,
      total : 0,
    };
  }

  componentDidMount() {
    this.fetchData(1);
  }

  fetchData = async() => {
    this.setState({loading : true})
    let data = await this.props.list()
    this.setState({
      data : data,
      loading : false,
      pageData : data.slice(0, 10),
      pageSize : 10,
      currentPage : 1,
      total : data.length
    })
  };

  editItem = async(record, e) => {
    this.props.edit(record.id)
  };

  deleteItem = async(record, e)=>{
    Dialog.confirm({
      content: "confirm to delete this data?",
      title: "warning",
      language : 'en-us',
      onOk: async() => {
        await this.props.del(record.id)
        Feedback.toast.success('Success');
        this.fetchData()
      }
    });
  }

  renderOperations = (value, index, record) => {
    let operation = { marginRight: '12px', textDecoration: 'none' }
    return (
      <div style={{ lineHeight: '28px' }}>
        <a href="javascript:void(0)" style={operation} onClick={() => { this.editItem(record); }}> Edit </a>
        <a href="javascript:void(0)" style={operation} onClick={() => { this.deleteItem(record); }}> Delete </a>
      </div>
    );
  };

  changePage = (currentPage) => {
    let data = this.state.data
    this.setState({ currentPage : currentPage, pageData:data.slice((currentPage-1) * 10, currentPage*10) });
  };

  render() { 
    return (
      <div className="simple-table">
        <IceContainer>
          <Table dataSource={this.state.pageData} isLoading={this.state.loading} className="basic-table" hasBorder={false} locale={{empty:"no data"}}>
            <Table.Column title="Name" dataIndex="name" width={50}/>
            <Table.Column title="From" dataIndex="from" width={80}/>
            <Table.Column title="To" dataIndex="to" width={80}/>
            <Table.Column title="CC" dataIndex="cc" width={80}/>
            <Table.Column title="Options" dataIndex="operation" width={80} cell={this.renderOperations}/>
          </Table>
          <div style={{ textAlign: 'right',paddingTop: '26px',}}>
            <Pagination current={this.state.currentPage} pageSize={this.state.pageSize} total={this.state.total} onChange={this.changePage} type={'normal'} locale={{next:'Next',prev:'Prev'}} />
          </div>
        </IceContainer>
      </div>
    );
  }
}