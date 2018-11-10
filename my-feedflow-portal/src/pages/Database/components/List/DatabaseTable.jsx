import React, { Component } from 'react';
import { Table, Pagination, Dialog, Feedback  } from '@icedesign/base';
import IceContainer from '@icedesign/container';
import PropTypes from 'prop-types';
import ContainerTitle from '../../../../components/ContainerTitle';

export default class DatabaseTable extends Component{
  static displayName='DatabaseTable';

  static propTypes = {
    list : PropTypes.func.isRequired,
  };

  static defaultProps = {}

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

  componentDidMount(){
    this.fetchData(1)
  }

  fetchData = async() => {
    this.setState({loading : true})
    let data = await this.props.list()
    this.setState({
      data : data,
      loading : false,
      pageData : data.slice(0,10),
      pageSize : 10,
      currentPage : 1,
      total : data.length
    })
  };

  render() { 
    return (
      <IceContainer  style={styles.container}>
        <Table dataSource={this.state.pageData} isLoading={this.state.loading} className="basic-table" hasBorder={false} locale={{empty:"no data"}}>
          <Table.Column title="Name" dataIndex="name" width={150}/>
          <Table.Column title="Type" dataIndex="type" width={85} />
          <Table.Column title="Server" dataIndex="server" width={150}/>
          <Table.Column title="User" dataIndex="user" width={85}/>
          <Table.Column title="Database" dataIndex="database" width={100}/>
          <Table.Column title="Options" dataIndex="operation" width={150} cell={this.renderOperations}/>
        </Table>
        <div style={{ textAlign: 'right',paddingTop: '26px',}}>
          <Pagination current={this.state.currentPage} pageSize={this.state.pageSize} total={this.state.total} onChange={this.changePage} type={'normal'} locale={{next:'Next',prev:'Prev'}} />
        </div>
      </IceContainer>
    );
  }
}

const styles = {
  container: {
    padding: '0',
  },
  title: {
    borderBottom: '0',
  },
  profile: {
    display: 'flex',
    alignItems: 'center',
  },
  avatar: {
    width: '24px',
    height: '24px',
    border: '1px solid #eee',
    background: '#eee',
    borderRadius: '50px',
  },
  name: {
    marginLeft: '15px',
    color: '#314659',
    fontSize: '14px',
  },
  link: {
    cursor: 'pointer',
    color: '#1890ff',
  },
  edit: {
    marginRight: '5px',
  },
};